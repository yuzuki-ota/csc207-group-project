package use_case.lookup;

import java.util.List;

public interface LookupOutputBoundary {
    /**
     * Shows the definitions found for a word.
     *
     * @param meanings definitions for the reader to choose from
     */
    void prepareSuccessView(List<LookupOutputData> meanings);

    /**
     * Clears the previous results and shows why the lookup failed.
     *
     * @param error message for the reader
     */
    void prepareFailView(String error);

    /**
     * Shows the meaning now in the deck.
     *
     * @param meaning card kept in the deck
     * @param alreadySaved whether the card was present before this save
     */
    void prepareSavedView(LookupOutputData meaning, boolean alreadySaved);

    /**
     * Shows a save error while keeping the current results available for retry.
     *
     * @param error message for the reader
     */
    void prepareSaveFailView(String error);
}
