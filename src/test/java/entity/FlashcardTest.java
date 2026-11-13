package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardTest {
    @Test
    void aNewCardCanBeRatedAgainAndGotIt() {
        final Flashcard card = new Flashcard("hello", "interjection", "A greeting.");
        assertEquals(Flashcard.NEW, card.getLastRating());
        card.recordRating(Flashcard.AGAIN);
        assertEquals(Flashcard.AGAIN, card.getLastRating());
        card.recordRating(Flashcard.GOT_IT);
        assertEquals(Flashcard.GOT_IT, card.getLastRating());
    }

    @Test
    void invalidRatingDoesNotReplaceTheLastRating() {
        final Flashcard card = new Flashcard("hello", "interjection", "A greeting.");
        card.recordRating(Flashcard.AGAIN);
        assertThrows(IllegalArgumentException.class, () -> card.recordRating("Easy"));
        assertEquals(Flashcard.AGAIN, card.getLastRating());
    }
}
