package use_case.review;

import java.io.IOException;

import entity.Deck;
import entity.Flashcard;

public interface ReviewDataAccessInterface {
    /**
     * Returns the saved deck, or an empty deck if nothing has been saved yet.
     *
     * @return the saved deck
     * @throws IOException if the saved deck cannot be read
     */
    Deck getDeck() throws IOException;

    /**
     * Saves the deck with this rating for the card, without changing the card itself.
     *
     * @param card the card that was rated
     * @param rating Again or Got it
     * @throws IOException if the deck cannot be saved
     */
    void saveRating(Flashcard card, String rating) throws IOException;
}
