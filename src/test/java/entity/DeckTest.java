package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void duplicateKeepsTheExistingCardAndRating() {
        final Deck deck = new Deck();
        final Flashcard card = new Flashcard("hello", "interjection", "A greeting.");
        deck.addCard(card);
        card.recordRating(Flashcard.AGAIN);

        final Flashcard duplicate = new Flashcard("Hello", "interjection", "A greeting.");
        assertSame(card, deck.addCard(duplicate));
        assertEquals(1, deck.getCards().size());
        assertEquals(Flashcard.AGAIN, deck.getCards().get(0).getLastRating());
    }

    @Test
    void differentMeaningsAndPartsOfSpeechAreKeptInOrder() {
        final Deck deck = new Deck();
        final Flashcard first = new Flashcard("book", "noun", "A written work.");
        final Flashcard second = new Flashcard("book", "noun", "A set of records.");
        final Flashcard third = new Flashcard("book", "verb", "Reserve something.");
        deck.addCard(first);
        deck.addCard(second);
        deck.addCard(third);

        assertEquals(3, deck.getCards().size());
        assertSame(first, deck.getCards().get(0));
        assertSame(second, deck.getCards().get(1));
        assertSame(third, deck.getCards().get(2));
    }

    @Test
    void changingTheReturnedListDoesNotRemoveSavedCards() {
        final Deck deck = new Deck();
        deck.addCard(new Flashcard("hello", "interjection", "A greeting."));
        deck.getCards().clear();
        assertEquals(1, deck.getCards().size());
    }
}
