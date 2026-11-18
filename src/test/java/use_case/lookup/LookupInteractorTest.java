package use_case.lookup;

import entity.Flashcard;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LookupInteractorTest {
    @Test
    void emptyInputDoesNotCallTheDictionary() {
        final DictionaryStub dictionary = new DictionaryStub();
        final TestPresenter presenter = new TestPresenter();
        final LookupInteractor interactor = new LookupInteractor(dictionary, presenter);

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

        new LookupInteractor(dictionary, presenter).execute(new LookupInputData(" book "));

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

        new LookupInteractor(dictionary, presenter).execute(new LookupInputData("unknownword"));

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
        final LookupInteractor interactor = new LookupInteractor(dictionary, presenter);

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

    private static class DictionaryStub implements LookupDataAccessInterface {
        private final List<Flashcard> meanings = new ArrayList<>();
        private int calls;
        private String word;
        private boolean fail;

        @Override
        public List<Flashcard> lookup(String word) throws IOException {
            calls++;
            this.word = word;
            if (fail) {
                throw new IOException("Connection failed.");
            }
            return meanings;
        }
    }

    private static class TestPresenter implements LookupOutputBoundary {
        private List<LookupOutputData> meanings;
        private String error;
        private int successes;
        private int failures;

        @Override
        public void prepareSuccessView(List<LookupOutputData> meanings) {
            this.meanings = meanings;
            successes++;
        }

        @Override
        public void prepareFailView(String error) {
            this.error = error;
            failures++;
        }
    }
}
