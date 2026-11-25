package use_case.review;

public class ReviewOutputData {
    private final String word;
    private final String partOfSpeech;
    private final String definition;

    public ReviewOutputData(String word, String partOfSpeech, String definition) {
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
