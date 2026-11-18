package use_case.lookup;

import entity.Flashcard;

import java.io.IOException;
import java.util.List;

public interface LookupDataAccessInterface {
    // Return an empty list if the dictionary has no entry for the word.
    List<Flashcard> lookup(String word) throws IOException;
}
