package use_case.review;

public interface ReviewOutputBoundary {
    void prepareCardView(ReviewOutputData card);

    void prepareSummaryView(ReviewSummaryOutputData summary);

    void prepareFailView(String error);
}
