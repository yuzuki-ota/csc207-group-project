package use_case.lookup;

import java.io.IOException;
import java.util.List;

import entity.Flashcard;

public interface LookupDataAccessInterface {
    /**
     * Gets the meanings listed by the dictionary.
     *
     * @param word word to look up
     * @return dictionary meanings, or an empty list if the word has no entry
     * @throws IOException if the dictionary response cannot be read
     */
    List<Flashcard> lookup(String word) throws IOException;
}
