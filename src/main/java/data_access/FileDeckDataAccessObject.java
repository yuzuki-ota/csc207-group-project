package data_access;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Deck;
import entity.Flashcard;
import use_case.lookup.LookupDeckDataAccessInterface;
import use_case.review.ReviewDataAccessInterface;

public class FileDeckDataAccessObject implements ReviewDataAccessInterface, LookupDeckDataAccessInterface {
    private static final String CARDS = "cards";
    private static final String WORD = "word";
    private static final String PART_OF_SPEECH = "partOfSpeech";
    private static final String DEFINITION = "definition";
    private static final String LAST_RATING = "lastRating";

    private final Path file;
    private Deck deck;

    public FileDeckDataAccessObject(String filePath) {
        this.file = Paths.get(filePath).toAbsolutePath();
    }

    @Override
    public Deck getDeck() throws IOException {
        if (deck == null) {
            deck = readDeck();
        }
        return deck;
    }

    @Override
    public Flashcard saveCard(Flashcard card) throws IOException {
        final Deck currentDeck = getDeck();
        Flashcard saved = currentDeck.findMeaning(card);
        if (saved == null) {
            final JSONArray cards = deckEntries(null, null);
            cards.put(toJson(card, card.getLastRating()));
            writeCards(cards);
            saved = currentDeck.addCard(card);
        }
        return saved;
    }

    @Override
    public void saveRating(Flashcard card, String rating) throws IOException {
        writeCards(deckEntries(card, rating));
    }

    private JSONArray deckEntries(Flashcard ratedCard, String rating) throws IOException {
        final JSONArray cards = new JSONArray();
        for (Flashcard savedCard : getDeck().getCards()) {
            if (ratedCard != null && savedCard.matchesMeaning(ratedCard)) {
                cards.put(toJson(savedCard, rating));
            }
            else {
                cards.put(toJson(savedCard, savedCard.getLastRating()));
            }
        }
        return cards;
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
        final Path temporary = Files.createTempFile(file.getParent(), "worddeck-", ".tmp");
        try {
            Files.write(temporary, deckJson.toString(2).getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }
}
