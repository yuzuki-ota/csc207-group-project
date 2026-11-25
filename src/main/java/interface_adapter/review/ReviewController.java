package interface_adapter.review;

import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInputData;

public class ReviewController {
    private final ReviewInputBoundary reviewInteractor;

    public ReviewController(ReviewInputBoundary reviewInteractor) {
        this.reviewInteractor = reviewInteractor;
    }

    public void startReview() {
        reviewInteractor.startReview();
    }

    public void rateCard(boolean gotIt) {
        reviewInteractor.rateCard(new ReviewInputData(gotIt));
    }

    public void endReview() {
        reviewInteractor.endReview();
    }
}
