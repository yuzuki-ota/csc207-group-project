package use_case.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import entity.Deck;
import entity.Flashcard;

class ReviewInteractorTest {
    @Test
    void emptyDeckAsksForAWordFirst() {
        final DeckStorageStub storage = new DeckStorageStub();
        final TestPresenter presenter = new TestPresenter();

        new ReviewInteractor(storage, presenter).startReview();

        assertTrue(presenter.words.isEmpty());
        assertNull(presenter.summary);
        assertEquals("Your deck is empty. Look up and save a word first.", presenter.error);
    }

    @Test
    void deckThatCannotBeOpenedShowsAnError() {
        final DeckStorageStub storage = new DeckStorageStub();
        storage.deck.addCard(new Flashcard("book", "noun", "A written work."));
        storage.failToOpen = true;
        final TestPresenter presenter = new TestPresenter();

        new ReviewInteractor(storage, presenter).startReview();

        assertTrue(presenter.words.isEmpty());
        assertEquals("Could not open the deck. Please try again.", presenter.error);
    }

    @Test
    void reviewShowsAgainCardsFirstAndEachCardOnce() {
        final DeckStorageStub storage = new DeckStorageStub();
        final Flashcard book = new Flashcard("book", "noun", "A written work.");
        final Flashcard read = new Flashcard("read", "verb", "Look at written words.");
        final Flashcard page = new Flashcard("page", "noun", "One side of a sheet.");
        book.recordRating(Flashcard.GOT_IT);
        read.recordRating(Flashcard.AGAIN);
        storage.deck.addCard(book);
        storage.deck.addCard(read);
        storage.deck.addCard(page);
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.startReview();
        assertEquals("read", presenter.card.getWord());
        assertEquals("verb", presenter.card.getPartOfSpeech());
        assertEquals("Look at written words.", presenter.card.getDefinition());

        interactor.rateCard(new ReviewInputData(true));
        interactor.rateCard(new ReviewInputData(false));
        interactor.rateCard(new ReviewInputData(true));

        assertEquals(Arrays.asList("read", "book", "page"), presenter.words);
        assertEquals(Arrays.asList("read Got it", "book Again", "page Got it"),
                storage.savedRatings);
        assertEquals(2, presenter.summary.getGotItCount());
        assertEquals(1, presenter.summary.getAgainCount());
        assertNull(presenter.error);
    }

    @Test
    void ratingIsRecordedOnTheCardAfterItIsSaved() {
        final DeckStorageStub storage = new DeckStorageStub();
        final Flashcard card = new Flashcard("hello", "interjection", "A greeting.");
        storage.deck.addCard(card);
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.startReview();
        interactor.rateCard(new ReviewInputData(false));

        assertEquals(Arrays.asList("hello Again"), storage.savedRatings);
        assertEquals(Flashcard.NEW, storage.ratingBeforeSave);
        assertEquals(Flashcard.AGAIN, card.getLastRating());
        assertEquals(0, presenter.summary.getGotItCount());
        assertEquals(1, presenter.summary.getAgainCount());
    }

    @Test
    void failedSaveStaysOnTheCardUntilARetryWorks() {
        final DeckStorageStub storage = new DeckStorageStub();
        final Flashcard book = new Flashcard("book", "noun", "A written work.");
        final Flashcard read = new Flashcard("read", "verb", "Look at written words.");
        storage.deck.addCard(book);
        storage.deck.addCard(read);
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.startReview();
        storage.failToSave = true;
        interactor.rateCard(new ReviewInputData(true));

        assertEquals("Could not save the rating. Please try again.", presenter.error);
        assertEquals(Arrays.asList("book"), presenter.words);
        assertEquals(Flashcard.NEW, book.getLastRating());

        storage.failToSave = false;
        interactor.rateCard(new ReviewInputData(false));
        interactor.endReview();

        assertEquals(Arrays.asList("book", "read"), presenter.words);
        assertEquals(Flashcard.AGAIN, book.getLastRating());
        assertEquals(0, presenter.summary.getGotItCount());
        assertEquals(1, presenter.summary.getAgainCount());
    }

    @Test
    void endingEarlyShowsTotalsForTheRatedCards() {
        final DeckStorageStub storage = new DeckStorageStub();
        storage.deck.addCard(new Flashcard("book", "noun", "A written work."));
        storage.deck.addCard(new Flashcard("read", "verb", "Look at written words."));
        storage.deck.addCard(new Flashcard("page", "noun", "One side of a sheet."));
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.startReview();
        interactor.rateCard(new ReviewInputData(true));
        interactor.endReview();

        assertEquals(1, presenter.summary.getGotItCount());
        assertEquals(0, presenter.summary.getAgainCount());

        interactor.rateCard(new ReviewInputData(true));
        assertEquals(Arrays.asList("book", "read"), presenter.words);
        assertEquals(1, storage.savedRatings.size());
    }

    @Test
    void nextReviewStartsWithTheCardsMarkedAgain() {
        final DeckStorageStub storage = new DeckStorageStub();
        storage.deck.addCard(new Flashcard("book", "noun", "A written work."));
        storage.deck.addCard(new Flashcard("read", "verb", "Look at written words."));
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.startReview();
        interactor.rateCard(new ReviewInputData(true));
        interactor.rateCard(new ReviewInputData(false));
        interactor.startReview();

        assertEquals(Arrays.asList("book", "read", "read"), presenter.words);
    }

    @Test
    void ratingOrEndingWithoutAReviewDoesNothing() {
        final DeckStorageStub storage = new DeckStorageStub();
        storage.deck.addCard(new Flashcard("book", "noun", "A written work."));
        final TestPresenter presenter = new TestPresenter();
        final ReviewInteractor interactor = new ReviewInteractor(storage, presenter);

        interactor.rateCard(new ReviewInputData(true));
        interactor.endReview();

        assertTrue(storage.savedRatings.isEmpty());
        assertTrue(presenter.words.isEmpty());
        assertNull(presenter.summary);
        assertNull(presenter.error);
    }

    private static class DeckStorageStub implements ReviewDataAccessInterface {
        private final Deck deck = new Deck();
        private final List<String> savedRatings = new ArrayList<>();
        private String ratingBeforeSave;
        private boolean failToOpen;
        private boolean failToSave;

        @Override
        public Deck getDeck() throws IOException {
            if (failToOpen) {
                throw new IOException("Could not read the deck file.");
            }
            return deck;
        }

        @Override
        public void saveRating(Flashcard card, String rating) throws IOException {
            if (failToSave) {
                throw new IOException("Could not write the deck file.");
            }
            ratingBeforeSave = card.getLastRating();
            savedRatings.add(card.getWord() + " " + rating);
        }
    }

    private static class TestPresenter implements ReviewOutputBoundary {
        private final List<String> words = new ArrayList<>();
        private ReviewOutputData card;
        private ReviewSummaryOutputData summary;
        private String error;

        @Override
        public void prepareCardView(ReviewOutputData card) {
            this.card = card;
            words.add(card.getWord());
        }

        @Override
        public void prepareSummaryView(ReviewSummaryOutputData summary) {
            this.summary = summary;
        }

        @Override
        public void prepareFailView(String error) {
            this.error = error;
        }
    }
}
