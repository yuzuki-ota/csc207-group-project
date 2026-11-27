package entity;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Flashcard> cards = new ArrayList<>();

    /**
     * Finds the saved card for a word, part of speech and definition.
     *
     * @param card meaning to find
     * @return the saved card, or null if the meaning is new
     */
    public Flashcard findMeaning(Flashcard card) {
        Flashcard match = null;
        for (Flashcard savedCard : cards) {
            if (savedCard.matchesMeaning(card)) {
                match = savedCard;
                break;
            }
        }
        return match;
    }

    /**
     * Adds a new meaning at the end of the deck, keeping the original card for a duplicate.
     *
     * @param card card to add
     * @return the card kept in the deck
     */
    public Flashcard addCard(Flashcard card) {
        Flashcard savedCard = findMeaning(card);
        if (savedCard == null) {
            cards.add(card);
            savedCard = card;
        }
        return savedCard;
    }

    public List<Flashcard> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Orders cards marked Again before the rest, preserving insertion order in each group.
     *
     * @return a new list in review order
     */
    public List<Flashcard> cardsForReview() {
        final List<Flashcard> orderedCards = new ArrayList<>();
        for (Flashcard card : cards) {
            if (Flashcard.AGAIN.equals(card.getLastRating())) {
                orderedCards.add(card);
            }
        }
        for (Flashcard card : cards) {
            if (!Flashcard.AGAIN.equals(card.getLastRating())) {
                orderedCards.add(card);
            }
        }
        return orderedCards;
    }
}
