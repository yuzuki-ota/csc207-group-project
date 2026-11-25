package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import entity.Deck;
import entity.Flashcard;

class FileDeckDataAccessObjectTest {
    private static final String TWO_CARDS = "{\"cards\": ["
            + "{\"word\": \"book\", \"partOfSpeech\": \"noun\","
            + " \"definition\": \"A written work.\", \"lastRating\": \"Got it\"},"
            + "{\"word\": \"read\", \"partOfSpeech\": \"verb\","
            + " \"definition\": \"Look at written words.\", \"lastRating\": \"New\"}"
            + "]}";

    @TempDir
    Path folder;

    @Test
    void missingFileGivesAnEmptyDeck() throws IOException {
        assertTrue(openStorage().getDeck().getCards().isEmpty());
    }

    @Test
    void cardsAreLoadedInOrderWithTheirRatings() throws IOException {
        writeDeckFile(TWO_CARDS);
        final Deck deck = openStorage().getDeck();

        assertEquals(2, deck.getCards().size());
        final Flashcard book = deck.getCards().get(0);
        assertEquals("book", book.getWord());
        assertEquals("noun", book.getPartOfSpeech());
        assertEquals("A written work.", book.getDefinition());
        assertEquals(Flashcard.GOT_IT, book.getLastRating());
        assertEquals("read", deck.getCards().get(1).getWord());
        assertEquals(Flashcard.NEW, deck.getCards().get(1).getLastRating());
    }

    @Test
    void cardWithoutRatingIsLoadedAsNew() throws IOException {
        writeDeckFile("{\"cards\": [{\"word\": \"hello\", \"partOfSpeech\": \"interjection\","
                + " \"definition\": \"A greeting.\"}]}");
        final Deck deck = openStorage().getDeck();

        assertEquals(1, deck.getCards().size());
        assertEquals(Flashcard.NEW, deck.getCards().get(0).getLastRating());
    }

    @Test
    void deckIsReadOnceAndShared() throws IOException {
        writeDeckFile(TWO_CARDS);
        final FileDeckDataAccessObject storage = openStorage();
        assertSame(storage.getDeck(), storage.getDeck());
    }

    @Test
    void savedRatingIsThereAfterReopeningTheFile() throws IOException {
        writeDeckFile(TWO_CARDS);
        final FileDeckDataAccessObject storage = openStorage();
        final Flashcard read = storage.getDeck().getCards().get(1);

        storage.saveRating(read, Flashcard.AGAIN);

        assertEquals(Flashcard.NEW, read.getLastRating());
        final Deck reopened = openStorage().getDeck();
        assertEquals(2, reopened.getCards().size());
        assertEquals("book", reopened.getCards().get(0).getWord());
        assertEquals(Flashcard.GOT_IT, reopened.getCards().get(0).getLastRating());
        assertEquals("read", reopened.getCards().get(1).getWord());
        assertEquals("Look at written words.", reopened.getCards().get(1).getDefinition());
        assertEquals(Flashcard.AGAIN, reopened.getCards().get(1).getLastRating());
    }

    @Test
    void fileThatIsNotJsonCannotBeOpened() throws IOException {
        writeDeckFile("not a deck");
        final FileDeckDataAccessObject storage = openStorage();
        assertThrows(IOException.class, storage::getDeck);
    }

    @Test
    void unknownRatingCannotBeOpened() throws IOException {
        writeDeckFile("{\"cards\": [{\"word\": \"book\", \"partOfSpeech\": \"noun\","
                + " \"definition\": \"A written work.\", \"lastRating\": \"Easy\"}]}");
        final FileDeckDataAccessObject storage = openStorage();
        assertThrows(IOException.class, storage::getDeck);
    }

    @Test
    void failedWriteThrowsAnException() {
        final String path = folder.resolve("missing").resolve("deck.json").toString();
        final FileDeckDataAccessObject storage = new FileDeckDataAccessObject(path);
        final Flashcard card = new Flashcard("book", "noun", "A written work.");
        assertThrows(IOException.class, () -> storage.saveRating(card, Flashcard.AGAIN));
    }

    private FileDeckDataAccessObject openStorage() {
        return new FileDeckDataAccessObject(deckPath().toString());
    }

    private Path deckPath() {
        return folder.resolve("deck.json");
    }

    private void writeDeckFile(String text) throws IOException {
        Files.write(deckPath(), text.getBytes(StandardCharsets.UTF_8));
    }
}
