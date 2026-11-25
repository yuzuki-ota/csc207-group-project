package interface_adapter.lookup;

import use_case.lookup.LookupInputBoundary;
import use_case.lookup.LookupInputData;

public class LookupController {
    private final LookupInputBoundary interactor;

    public LookupController(LookupInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Requests dictionary entries for the search field's text.
     *
     * @param word the word to look up
     */
    public void lookUp(String word) {
        interactor.execute(new LookupInputData(word));
    }

    /**
     * Saves a meaning from the latest lookup.
     *
     * @param selectedIndex the selected row, starting at zero
     */
    public void saveMeaning(int selectedIndex) {
        interactor.saveMeaning(selectedIndex);
    }
}
