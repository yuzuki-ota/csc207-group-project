package use_case.lookup;

import entity.Flashcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LookupInteractor implements LookupInputBoundary {
    private final LookupDataAccessInterface dictionary;
    private final LookupOutputBoundary presenter;

    public LookupInteractor(LookupDataAccessInterface dictionary, LookupOutputBoundary presenter) {
        this.dictionary = dictionary;
        this.presenter = presenter;
    }

    @Override
    public void execute(LookupInputData inputData) {
        final String word = inputData.getWord().trim();
        if (word.isEmpty()) {
            presenter.prepareFailView("Enter a word.");
            return;
        }

        final List<Flashcard> meanings;
        try {
            meanings = dictionary.lookup(word);
        }
        catch (IOException exception) {
            presenter.prepareFailView("Could not look up the word. Please try again.");
            return;
        }

        if (meanings.isEmpty()) {
            presenter.prepareFailView("No definitions found for " + word + ".");
        }
        else {
            final List<LookupOutputData> outputData = new ArrayList<>();
            for (Flashcard meaning : meanings) {
                outputData.add(new LookupOutputData(meaning.getWord(),
                        meaning.getPartOfSpeech(), meaning.getDefinition()));
            }
            presenter.prepareSuccessView(outputData);
        }
    }
}
