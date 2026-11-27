package use_case.lookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Flashcard;

public class LookupInteractor implements LookupInputBoundary {
    private final LookupDataAccessInterface dictionary;
    private final LookupDeckDataAccessInterface deckStorage;
    private final LookupOutputBoundary presenter;
    private List<LookupOutputData> results = Collections.emptyList();

    public LookupInteractor(LookupDataAccessInterface dictionary, LookupDeckDataAccessInterface deckStorage,
                            LookupOutputBoundary presenter) {
        this.dictionary = dictionary;
        this.deckStorage = deckStorage;
        this.presenter = presenter;
    }

    @Override
    public void execute(LookupInputData inputData) {
        results = Collections.emptyList();
        final String word = inputData.getWord().trim();
        if (word.isEmpty()) {
            presenter.prepareFailView("Enter a word.");
        }
        else {
            try {
                showMeanings(word, dictionary.lookup(word));
            }
            catch (IOException exception) {
                presenter.prepareFailView("Could not look up the word. Please try again.");
            }
        }
    }

    private void showMeanings(String word, List<Flashcard> meanings) {
        if (meanings.isEmpty()) {
            presenter.prepareFailView("No definitions found for " + word + ".");
        }
        else {
            final List<LookupOutputData> outputData = new ArrayList<>();
            for (Flashcard meaning : meanings) {
                outputData.add(toOutputData(meaning));
            }
            results = Collections.unmodifiableList(outputData);
            presenter.prepareSuccessView(results);
        }
    }

    @Override
    public void saveMeaning(int index) {
        if (index < 0 || index >= results.size()) {
            presenter.prepareSaveFailView("Choose a definition to save.");
        }
        else {
            final LookupOutputData selected = results.get(index);
            final Flashcard card = new Flashcard(selected.getWord(), selected.getPartOfSpeech(),
                    selected.getDefinition());
            try {
                final Flashcard savedCard = deckStorage.saveCard(card);
                presenter.prepareSavedView(toOutputData(savedCard), savedCard != card);
            }
            catch (IOException exception) {
                presenter.prepareSaveFailView("Could not save the card. Please try again.");
            }
        }
    }

    private LookupOutputData toOutputData(Flashcard card) {
        return new LookupOutputData(card.getWord(), card.getPartOfSpeech(), card.getDefinition());
    }
}
