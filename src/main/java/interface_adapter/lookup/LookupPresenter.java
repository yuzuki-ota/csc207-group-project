package interface_adapter.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import use_case.lookup.LookupOutputBoundary;
import use_case.lookup.LookupOutputData;

public class LookupPresenter implements LookupOutputBoundary {
    private final LookupViewModel viewModel;

    public LookupPresenter(LookupViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(List<LookupOutputData> meanings) {
        final List<String> results = new ArrayList<>();
        for (LookupOutputData meaning : meanings) {
            results.add(meaning.getWord() + " (" + meaning.getPartOfSpeech() + ") - "
                    + meaning.getDefinition());
        }
        viewModel.showResults(results, "Select the meaning you want to save.");
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.showResults(Collections.emptyList(), error);
    }

    @Override
    public void prepareSavedView(LookupOutputData meaning, boolean alreadySaved) {
        if (alreadySaved) {
            viewModel.showMessage("This meaning of " + meaning.getWord() + " is already in your deck.");
        }
        else {
            viewModel.showMessage("Saved this meaning of " + meaning.getWord() + ".");
        }
    }

    @Override
    public void prepareSaveFailView(String error) {
        viewModel.showMessage(error);
    }
}
