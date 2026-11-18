package entity;

public class Flashcard {
    public static final String NEW = "New";
    public static final String AGAIN = "Again";
    public static final String GOT_IT = "Got it";

    private final String word;
    private final String partOfSpeech;
    private final String definition;
    private String lastRating = NEW;

    public Flashcard(String word, String partOfSpeech, String definition) {
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

    public String getLastRating() {
        return lastRating;
    }

    public boolean matchesMeaning(Flashcard other) {
        return word.equalsIgnoreCase(other.word)
                && partOfSpeech.equals(other.partOfSpeech)
                && definition.equals(other.definition);
    }

    public void recordRating(String rating) {
        if (!AGAIN.equals(rating) && !GOT_IT.equals(rating)) {
            throw new IllegalArgumentException("Choose Again or Got it.");
        }
        lastRating = rating;
    }
}
