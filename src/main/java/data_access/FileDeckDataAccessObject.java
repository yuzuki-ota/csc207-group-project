package data_access;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Deck;
import entity.Flashcard;
import use_case.review.ReviewDataAccessInterface;

public class FileDeckDataAccessObject implements ReviewDataAccessInterface {
    private static final String CARDS = "cards";
    private static final String WORD = "word";
    private static final String PART_OF_SPEECH = "partOfSpeech";
    private static final String DEFINITION = "definition";
    private static final String LAST_RATING = "lastRating";

    private final Path file;
    private Deck deck;

    public FileDeckDataAccessObject(String filePath) {
        this.file = Paths.get(filePath);
    }

    @Override
    public Deck getDeck() throws IOException {
        if (deck == null) {
            deck = readDeck();
        }
        return deck;
    }

    @Override
    public void saveRating(Flashcard card, String rating) throws IOException {
        final JSONArray cards = new JSONArray();
        for (Flashcard savedCard : getDeck().getCards()) {
            if (savedCard.matchesMeaning(card)) {
                cards.put(toJson(savedCard, rating));
            }
            else {
                cards.put(toJson(savedCard, savedCard.getLastRating()));
            }
        }
        writeCards(cards);
    }

    private Deck readDeck() throws IOException {
        final Deck savedDeck = new Deck();
        if (Files.exists(file)) {
            final String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            try {
                final JSONArray cards = new JSONObject(text).getJSONArray(CARDS);
                for (int i = 0; i < cards.length(); i++) {
                    savedDeck.addCard(toCard(cards.getJSONObject(i)));
                }
            }
            catch (JSONException | IllegalArgumentException exception) {
                throw new IOException("The deck file is not in the expected format.", exception);
            }
        }
        return savedDeck;
    }

    private Flashcard toCard(JSONObject json) {
        final Flashcard card = new Flashcard(json.getString(WORD), json.getString(PART_OF_SPEECH),
                json.getString(DEFINITION));
        final String lastRating = json.optString(LAST_RATING, Flashcard.NEW);
        if (!Flashcard.NEW.equals(lastRating)) {
            card.recordRating(lastRating);
        }
        return card;
    }

    private JSONObject toJson(Flashcard card, String lastRating) {
        final JSONObject json = new JSONObject();
        json.put(WORD, card.getWord());
        json.put(PART_OF_SPEECH, card.getPartOfSpeech());
        json.put(DEFINITION, card.getDefinition());
        json.put(LAST_RATING, lastRating);
        return json;
    }

    private void writeCards(JSONArray cards) throws IOException {
        final JSONObject deckJson = new JSONObject();
        deckJson.put(CARDS, cards);
        Files.write(file, deckJson.toString(2).getBytes(StandardCharsets.UTF_8));
    }
}
