package use_case.review;

import java.io.IOException;

import entity.Deck;
import entity.Flashcard;

public interface ReviewDataAccessInterface {
    // Return an empty deck if nothing has been saved yet.
    Deck getDeck() throws IOException;

    // Save the deck with this rating for the card, without changing the card itself.
    void saveRating(Flashcard card, String rating) throws IOException;
}
