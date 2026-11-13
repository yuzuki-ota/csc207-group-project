package entity;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Flashcard> cards = new ArrayList<>();

    public Flashcard addCard(Flashcard card) {
        for (Flashcard savedCard : cards) {
            if (savedCard.matchesMeaning(card)) {
                return savedCard;
            }
        }
        cards.add(card);
        return card;
    }

    public List<Flashcard> getCards() {
        return new ArrayList<>(cards);
    }

}
