package use_case.review;

public interface ReviewOutputBoundary {
    /**
     * Prepares the view for the card being reviewed.
     *
     * @param card the word, part of speech, and definition to show
     */
    void prepareCardView(ReviewOutputData card);

    /**
     * Prepares the view for the end of a review.
     *
     * @param summary how many cards got each rating
     */
    void prepareSummaryView(ReviewSummaryOutputData summary);

    /**
     * Prepares the failure view.
     *
     * @param error the explanation of the failure
     */
    void prepareFailView(String error);
}
