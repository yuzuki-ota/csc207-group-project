package use_case.lookup;

public class LookupOutputData {
    private final String word;
    private final String partOfSpeech;
    private final String definition;

    public LookupOutputData(String word, String partOfSpeech, String definition) {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }
}
