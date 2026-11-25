package interface_adapter.review;

public class ReviewState {
    private boolean reviewing;
    private String word = "";
    private String partOfSpeech = "";
    private String definition = "";
    private boolean definitionShown;
    private String message = "";

    public boolean isReviewing() {
        return reviewing;
    }

    public void setReviewing(boolean reviewing) {
        this.reviewing = reviewing;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public boolean isDefinitionShown() {
        return definitionShown;
    }

    public void setDefinitionShown(boolean definitionShown) {
        this.definitionShown = definitionShown;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
