package use_case.review;

import java.io.IOException;
import java.util.List;

import entity.Flashcard;
import entity.ReviewSession;

public class ReviewInteractor implements ReviewInputBoundary {
    private final ReviewDataAccessInterface deckStorage;
    private final ReviewOutputBoundary presenter;
    private ReviewSession session;

    public ReviewInteractor(ReviewDataAccessInterface deckStorage, ReviewOutputBoundary presenter) {
        this.deckStorage = deckStorage;
        this.presenter = presenter;
    }

    @Override
    public void startReview() {
        try {
            final List<Flashcard> cards = deckStorage.getDeck().cardsForReview();
            if (cards.isEmpty()) {
                presenter.prepareFailView("Your deck is empty. Look up and save a word first.");
            }
            else {
                session = new ReviewSession(cards);
                showCurrentCard();
            }
        }
        catch (IOException exception) {
            presenter.prepareFailView("Could not open the deck. Please try again.");
        }
    }

    @Override
    public void rateCard(ReviewInputData inputData) {
        if (session != null) {
            final String rating;
            if (inputData.isGotIt()) {
                rating = Flashcard.GOT_IT;
            }
            else {
                rating = Flashcard.AGAIN;
            }
            saveAndMoveOn(rating);
        }
    }

    @Override
    public void endReview() {
        if (session != null) {
            showSummary();
        }
    }

    private void saveAndMoveOn(String rating) {
        final Flashcard card = session.currentCard();
        try {
            deckStorage.saveRating(card, rating);
            card.recordRating(rating);
            session.advance(rating);
            if (session.isFinished()) {
                showSummary();
            }
            else {
                showCurrentCard();
            }
        }
        catch (IOException exception) {
            presenter.prepareFailView("Could not save the rating. Please try again.");
        }
    }

    private void showCurrentCard() {
        final Flashcard card = session.currentCard();
        presenter.prepareCardView(new ReviewOutputData(card.getWord(), card.getPartOfSpeech(),
                card.getDefinition()));
    }

    private void showSummary() {
        presenter.prepareSummaryView(new ReviewSummaryOutputData(session.getGotItCount(),
                session.getAgainCount()));
        session = null;
    }
}
