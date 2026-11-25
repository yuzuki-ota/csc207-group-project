package interface_adapter.review;

import org.junit.jupiter.api.Test;
import use_case.review.ReviewOutputData;
import use_case.review.ReviewSummaryOutputData;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewPresenterTest {
    @Test
    void newCardStartsWithTheDefinitionHidden() {
        final ReviewViewModel viewModel = new ReviewViewModel();
        final List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        final ReviewPresenter presenter = new ReviewPresenter(viewModel);
        presenter.prepareCardView(new ReviewOutputData("book", "noun", "A written work."));
        viewModel.getState().setDefinitionShown(true);

        presenter.prepareCardView(new ReviewOutputData("read", "verb", "Look at written words."));

        final ReviewState state = viewModel.getState();
        assertTrue(state.isReviewing());
        assertEquals("read", state.getWord());
        assertEquals("verb", state.getPartOfSpeech());
        assertEquals("Look at written words.", state.getDefinition());
        assertFalse(state.isDefinitionShown());
        assertEquals("", state.getMessage());
        assertEquals(2, events.size());
    }

    @Test
    void failedSaveKeepsTheCardOnScreen() {
        final ReviewViewModel viewModel = new ReviewViewModel();
        final ReviewPresenter presenter = new ReviewPresenter(viewModel);
        presenter.prepareCardView(new ReviewOutputData("book", "noun", "A written work."));
        viewModel.getState().setDefinitionShown(true);

        presenter.prepareFailView("Could not save the rating. Please try again.");

        final ReviewState state = viewModel.getState();
        assertTrue(state.isReviewing());
        assertEquals("book", state.getWord());
        assertTrue(state.isDefinitionShown());
        assertEquals("Could not save the rating. Please try again.", state.getMessage());
    }

    @Test
    void summaryEndsTheReviewAndShowsTheTotals() {
        final ReviewViewModel viewModel = new ReviewViewModel();
        final ReviewPresenter presenter = new ReviewPresenter(viewModel);
        presenter.prepareCardView(new ReviewOutputData("book", "noun", "A written work."));

        presenter.prepareSummaryView(new ReviewSummaryOutputData(2, 1));

        final ReviewState state = viewModel.getState();
        assertFalse(state.isReviewing());
        assertEquals("", state.getWord());
        assertEquals("", state.getDefinition());
        assertEquals("Review finished. Got it: 2, Again: 1", state.getMessage());
    }
}
