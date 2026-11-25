package interface_adapter.review;

import use_case.review.ReviewOutputBoundary;
import use_case.review.ReviewOutputData;
import use_case.review.ReviewSummaryOutputData;

public class ReviewPresenter implements ReviewOutputBoundary {
    private final ReviewViewModel reviewViewModel;

    public ReviewPresenter(ReviewViewModel reviewViewModel) {
        this.reviewViewModel = reviewViewModel;
    }

    @Override
    public void prepareCardView(ReviewOutputData card) {
        final ReviewState state = new ReviewState();
        state.setReviewing(true);
        state.setWord(card.getWord());
        state.setPartOfSpeech(card.getPartOfSpeech());
        state.setDefinition(card.getDefinition());
        reviewViewModel.setState(state);
        reviewViewModel.firePropertyChanged();
    }

    @Override
    public void prepareSummaryView(ReviewSummaryOutputData summary) {
        final ReviewState state = new ReviewState();
        state.setMessage("Review finished. Got it: " + summary.getGotItCount()
                + ", Again: " + summary.getAgainCount());
        reviewViewModel.setState(state);
        reviewViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        reviewViewModel.getState().setMessage(error);
        reviewViewModel.firePropertyChanged();
    }
}
