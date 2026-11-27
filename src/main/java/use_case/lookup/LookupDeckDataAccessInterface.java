package use_case.lookup;

import java.io.IOException;

import entity.Flashcard;

public interface LookupDeckDataAccessInterface {
    /**
     * Saves a new meaning while keeping any previously saved rating for a duplicate.
     *
     * @param card new card to save
     * @return the input card after insertion, or the existing card if the meaning was already saved
     * @throws IOException if the deck cannot be read or written
     */
    Flashcard saveCard(Flashcard card) throws IOException;
}
