package use_case.review;

public interface ReviewInputBoundary {
    void startReview();

    void rateCard(ReviewInputData inputData);

    void endReview();
}
