package use_case.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import entity.Deck;
import entity.Flashcard;

class LookupInteractorTest {
    private final DeckStorageStub deckStorage = new DeckStorageStub();

    @Test
    void emptyInputDoesNotCallTheDictionary() {
        final DictionaryStub dictionary = new DictionaryStub();
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, deckStorage, presenter);

        interactor.execute(new LookupInputData("   "));

        assertEquals(0, dictionary.calls);
        assertEquals(0, presenter.successes);
        assertEquals(1, presenter.failures);
        assertEquals("Enter a word.", presenter.error);
    }

    @Test
    void lookupTrimsInputAndReturnsAllMeanings() {
        final DictionaryStub dictionary = new DictionaryStub();
        dictionary.meanings.add(new Flashcard("book", "noun", "A written work."));
        dictionary.meanings.add(new Flashcard("book", "verb", "Reserve something."));
        final TestPresenter presenter = new TestPresenter();

        new LookupInteractor(dictionary, deckStorage, presenter).execute(new LookupInputData(" book "));

        assertEquals(1, dictionary.calls);
        assertEquals("book", dictionary.word);
        assertEquals(1, presenter.successes);
        assertEquals(0, presenter.failures);
        assertEquals(2, presenter.meanings.size());
        assertEquals("book", presenter.meanings.get(0).getWord());
        assertEquals("noun", presenter.meanings.get(0).getPartOfSpeech());
        assertEquals("A written work.", presenter.meanings.get(0).getDefinition());
        assertEquals("book", presenter.meanings.get(1).getWord());
        assertEquals("verb", presenter.meanings.get(1).getPartOfSpeech());
        assertEquals("Reserve something.", presenter.meanings.get(1).getDefinition());
    }

    @Test
    void missingWordShowsAnError() {
        final DictionaryStub dictionary = new DictionaryStub();
        final TestPresenter presenter = new TestPresenter();

        new LookupInteractor(dictionary, deckStorage, presenter).execute(new LookupInputData("unknownword"));

        assertEquals(1, dictionary.calls);
        assertEquals(0, presenter.successes);
        assertEquals(1, presenter.failures);
        assertEquals("No definitions found for unknownword.", presenter.error);
    }

    @Test
    void failedLookupCanBeRetried() {
        final DictionaryStub dictionary = new DictionaryStub();
        dictionary.fail = true;
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, deckStorage, presenter);

        interactor.execute(new LookupInputData("hello"));

        assertEquals(0, presenter.successes);
        assertEquals(1, presenter.failures);
        assertEquals("Could not look up the word. Please try again.", presenter.error);

        dictionary.fail = false;
        dictionary.meanings.add(new Flashcard("hello", "interjection", "A greeting."));
        interactor.execute(new LookupInputData("hello"));

        assertEquals(2, dictionary.calls);
        assertEquals(1, presenter.successes);
        assertEquals(1, presenter.failures);
        assertEquals("hello", presenter.meanings.get(0).getWord());
    }

    @Test
    void savesTheSelectedMeaningAndRecognizesItWhenSavedAgain() {
        final DictionaryStub dictionary = new DictionaryStub();
        dictionary.meanings.add(new Flashcard("book", "noun", "A written work."));
        dictionary.meanings.add(new Flashcard("book", "verb", "Reserve something."));
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, deckStorage, presenter);
        interactor.execute(new LookupInputData("book"));

        interactor.saveMeaning(1);

        assertEquals(1, deckStorage.deck.getCards().size());
        final Flashcard card = deckStorage.deck.getCards().get(0);
        assertEquals("book", card.getWord());
        assertEquals("verb", card.getPartOfSpeech());
        assertEquals("Reserve something.", card.getDefinition());
        assertEquals(Flashcard.NEW, card.getLastRating());
        assertFalse(presenter.alreadySaved);
        assertEquals("Reserve something.", presenter.saved.getDefinition());

        card.recordRating(Flashcard.AGAIN);
        interactor.saveMeaning(1);

        assertTrue(presenter.alreadySaved);
        assertEquals(1, deckStorage.deck.getCards().size());
        assertEquals(Flashcard.AGAIN, card.getLastRating());
        interactor.saveMeaning(0);
        assertFalse(presenter.alreadySaved);
        assertEquals(2, deckStorage.deck.getCards().size());
    }

    @Test
    void savingCanBeRetriedWithoutRepeatingTheLookup() {
        final DictionaryStub dictionary = new DictionaryStub();
        dictionary.meanings.add(new Flashcard("book", "noun", "A written work."));
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, deckStorage, presenter);
        interactor.execute(new LookupInputData("book"));

        deckStorage.fail = true;
        interactor.saveMeaning(0);

        assertNull(presenter.saved);
        assertEquals("Could not save the card. Please try again.", presenter.saveError);
        assertTrue(deckStorage.deck.getCards().isEmpty());

        deckStorage.fail = false;
        interactor.saveMeaning(0);
        assertEquals("A written work.", presenter.saved.getDefinition());
        assertEquals(1, deckStorage.deck.getCards().size());
        assertEquals(1, dictionary.calls);
    }

    @Test
    void rejectsMissingSelectionsAndSelectionsFromAnEarlierLookup() {
        final DictionaryStub dictionary = new DictionaryStub();
        dictionary.meanings.add(new Flashcard("book", "noun", "A written work."));
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, deckStorage, presenter);
        interactor.execute(new LookupInputData("book"));

        interactor.saveMeaning(-1);
        assertEquals("Choose a definition to save.", presenter.saveError);

        dictionary.meanings.clear();
        interactor.execute(new LookupInputData("unknownword"));
        presenter.saveError = null;
        interactor.saveMeaning(0);
        assertEquals("Choose a definition to save.", presenter.saveError);
        assertTrue(deckStorage.deck.getCards().isEmpty());
    }

    private static final class DeckStorageStub implements LookupDeckDataAccessInterface {
        private final Deck deck = new Deck();
        private boolean fail;

        @Override
        public Flashcard saveCard(Flashcard card) throws IOException {
            if (fail) {
                throw new IOException("Could not write to disk.");
            }
            return deck.addCard(card);
        }
    }

    private static final class DictionaryStub implements LookupDataAccessInterface {
        private final List<Flashcard> meanings = new ArrayList<>();
        private int calls;
        private String word;
        private boolean fail;

        @Override
        public List<Flashcard> lookup(String query) throws IOException {
            calls++;
            word = query;
            if (fail) {
                throw new IOException("Connection failed.");
            }
            return meanings;
        }
    }

    private static final class TestPresenter implements LookupOutputBoundary {
        private List<LookupOutputData> meanings;
        private String error;
        private String saveError;
        private LookupOutputData saved;
        private boolean alreadySaved;
        private int successes;
        private int failures;

        @Override
        public void prepareSuccessView(List<LookupOutputData> results) {
            meanings = results;
            successes++;
        }

        @Override
        public void prepareFailView(String message) {
            error = message;
            failures++;
        }

        @Override
        public void prepareSavedView(LookupOutputData meaning, boolean duplicate) {
            saved = meaning;
            alreadySaved = duplicate;
        }

        @Override
        public void prepareSaveFailView(String message) {
            saveError = message;
        }
    }
}
