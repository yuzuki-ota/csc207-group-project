package entity;

import java.util.ArrayList;
import java.util.List;

public class ReviewSession {
    private final List<Flashcard> orderedCards;
    private int currentIndex;
    private int gotItCount;
    private int againCount;

    public ReviewSession(List<Flashcard> orderedCards) {
        this.orderedCards = new ArrayList<>(orderedCards);
    }

    public Flashcard currentCard() {
        if (isFinished()) {
            return null;
        }
        return orderedCards.get(currentIndex);
    }

    public void advance(String rating) {
        if (isFinished()) {
            throw new IllegalStateException("The review session has finished.");
        }
        if (Flashcard.AGAIN.equals(rating)) {
            againCount++;
        }
        else if (Flashcard.GOT_IT.equals(rating)) {
            gotItCount++;
        }
        else {
            throw new IllegalArgumentException("Choose Again or Got it.");
        }
        currentIndex++;
    }

    public boolean isFinished() {
        return currentIndex >= orderedCards.size();
    }

    public int getGotItCount() {
        return gotItCount;
    }

    public int getAgainCount() {
        return againCount;
    }
}
