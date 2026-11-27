package use_case.lookup;

public interface LookupInputBoundary {
    /**
     * Looks up the definitions of the entered word.
     *
     * @param inputData the word to look up
     */
    void execute(LookupInputData inputData);

    /**
     * Saves a meaning from the latest lookup.
     *
     * @param index zero-based position of the selected definition
     */
    void saveMeaning(int index);
}
