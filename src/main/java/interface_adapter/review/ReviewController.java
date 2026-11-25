package interface_adapter.review;

import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInputData;

public class ReviewController {
    private final ReviewInputBoundary reviewInteractor;

    public ReviewController(ReviewInputBoundary reviewInteractor) {
        this.reviewInteractor = reviewInteractor;
    }

    /**
     * Starts a review of the saved deck.
     */
    public void startReview() {
        reviewInteractor.startReview();
    }

    /**
     * Rates the current card.
     *
     * @param gotIt true for Got it and false for Again
     */
    public void rateCard(boolean gotIt) {
        reviewInteractor.rateCard(new ReviewInputData(gotIt));
    }

    /**
     * Ends the review before the last card.
     */
    public void endReview() {
        reviewInteractor.endReview();
    }
}
