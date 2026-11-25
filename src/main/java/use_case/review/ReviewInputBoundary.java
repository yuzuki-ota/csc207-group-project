package use_case.review;

public interface ReviewInputBoundary {
    /**
     * Starts a review of the saved deck.
     */
    void startReview();

    /**
     * Saves the rating for the current card and moves to the next one.
     *
     * @param inputData whether the learner got the card
     */
    void rateCard(ReviewInputData inputData);

    /**
     * Ends the review before the last card and shows the totals so far.
     */
    void endReview();
}
