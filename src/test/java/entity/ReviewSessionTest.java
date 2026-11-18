package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewSessionTest {
    @Test
    void visitsEachCardOnceAndCountsTheRatings() {
        final Flashcard first = new Flashcard("book", "noun", "A written work.");
        final Flashcard second = new Flashcard("read", "verb", "Look at written words.");
        final ReviewSession session = new ReviewSession(Arrays.asList(first, second));

        assertFalse(session.isFinished());
        assertSame(first, session.currentCard());
        session.advance(Flashcard.GOT_IT);
        assertSame(second, session.currentCard());
        session.advance(Flashcard.AGAIN);

        assertTrue(session.isFinished());
        assertNull(session.currentCard());
        assertEquals(1, session.getGotItCount());
        assertEquals(1, session.getAgainCount());
        assertThrows(IllegalStateException.class, () -> session.advance(Flashcard.AGAIN));
        assertEquals(1, session.getAgainCount());
    }

    @Test
    void emptyDeckStartsWithAFinishedSession() {
        final ReviewSession session = new ReviewSession(new Deck().cardsForReview());
        assertTrue(session.isFinished());
        assertNull(session.currentCard());
        assertEquals(0, session.getGotItCount());
        assertEquals(0, session.getAgainCount());
    }

    @Test
    void invalidRatingDoesNotAdvanceTheSession() {
        final Flashcard card = new Flashcard("book", "noun", "A written work.");
        final ReviewSession session = new ReviewSession(Arrays.asList(card));

        assertThrows(IllegalArgumentException.class, () -> session.advance(Flashcard.NEW));
        assertSame(card, session.currentCard());
        assertEquals(0, session.getGotItCount());
        assertEquals(0, session.getAgainCount());
        session.advance(Flashcard.GOT_IT);
        assertTrue(session.isFinished());
        assertEquals(1, session.getGotItCount());
    }

    @Test
    void changingTheInputListDoesNotChangeTheSession() {
        final Flashcard card = new Flashcard("book", "noun", "A written work.");
        final List<Flashcard> cards = new ArrayList<>();
        cards.add(card);
        final ReviewSession session = new ReviewSession(cards);
        cards.clear();

        assertSame(card, session.currentCard());
        session.advance(Flashcard.AGAIN);
        assertTrue(session.isFinished());
    }
}
