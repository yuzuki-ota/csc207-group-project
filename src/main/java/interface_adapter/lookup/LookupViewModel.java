package interface_adapter.lookup;

import java.util.Collections;
import java.util.List;

import interface_adapter.ViewModel;

public class LookupViewModel extends ViewModel<List<String>> {
    private String message = "Enter a word to look up.";

    public LookupViewModel() {
        super(Collections.emptyList());
    }

    public String getMessage() {
        return message;
    }

    /**
     * Replaces all definitions after a search.
     *
     * @param results formatted definitions in their original order
     * @param status the message to display alongside them
     */
    public void showResults(List<String> results, String status) {
        setState(results);
        showMessage(status);
    }

    /**
     * Updates the status while keeping the search results.
     *
     * @param status the new message
     */
    public void showMessage(String status) {
        message = status;
        firePropertyChanged();
    }
}
