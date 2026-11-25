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

    /**
     * Gets the next card waiting for a rating.
     *
     * @return the current card, or null when the session is finished
     */
    public Flashcard currentCard() {
        Flashcard current = null;
        if (!isFinished()) {
            current = orderedCards.get(currentIndex);
        }
        return current;
    }

    /**
     * Counts the rating and advances to the next card in this session.
     *
     * @param rating Again or Got it
     * @throws IllegalStateException if the session has finished
     * @throws IllegalArgumentException if the rating is not a review choice
     */
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
