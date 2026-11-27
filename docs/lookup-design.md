# Look up and save a word

`LookupInteractor` asks the dictionary for meanings and the deck to save the one the reader selects. Each operation has its own data access interface. `app.Main` gives the lookup and review interactors the same `FileDeckDataAccessObject`, so a saved meaning is available the next time a review starts.

## Class diagram

```mermaid
classDiagram
    direction LR
    namespace view {
        class LookupView
    }
    namespace interface_adapter {
        class LookupController
        class LookupPresenter
        class LookupViewModel
    }
    namespace use_case {
        class LookupInputBoundary {
            <<interface>>
            +execute(LookupInputData inputData)
            +saveMeaning(int index)
        }
        class LookupInputData
        class LookupInteractor
        class LookupOutputBoundary {
            <<interface>>
        }
        class LookupOutputData
        class LookupDataAccessInterface {
            <<interface>>
            +lookup(String word) List~Flashcard~
        }
        class LookupDeckDataAccessInterface {
            <<interface>>
            +saveCard(Flashcard card) Flashcard
        }
    }
    namespace data_access {
        class DictionaryDataAccessObject
        class FileDeckDataAccessObject
    }
    namespace entity {
        class Deck
        class Flashcard
    }

    LookupView --> LookupController
    LookupView --> LookupViewModel
    LookupController --> LookupInputBoundary
    LookupController ..> LookupInputData
    LookupInteractor ..|> LookupInputBoundary
    LookupInteractor --> LookupOutputBoundary
    LookupInteractor --> LookupOutputData
    LookupInteractor --> LookupDataAccessInterface
    LookupInteractor --> LookupDeckDataAccessInterface
    LookupInteractor ..> Flashcard
    LookupPresenter ..|> LookupOutputBoundary
    LookupPresenter --> LookupViewModel
    DictionaryDataAccessObject ..|> LookupDataAccessInterface
    DictionaryDataAccessObject ..> Flashcard
    FileDeckDataAccessObject ..|> LookupDeckDataAccessInterface
    FileDeckDataAccessObject --> Deck
    Deck o-- Flashcard
```

## Looking up a word and saving one meaning

```mermaid
sequenceDiagram
    actor Reader
    participant View as LookupView
    participant Controller as LookupController
    participant Interactor as LookupInteractor
    participant Dictionary as DictionaryDataAccessObject
    participant Storage as FileDeckDataAccessObject
    participant Presenter as LookupPresenter
    participant ViewModel as LookupViewModel

    Reader->>View: Enter a word and click Look up
    View->>Controller: lookUp(word)
    Controller->>Interactor: execute(LookupInputData)
    Interactor->>Dictionary: lookup(word)
    Dictionary-->>Interactor: One Flashcard per definition
    Interactor->>Presenter: prepareSuccessView(meanings)
    Presenter->>ViewModel: showResults(results, status)
    ViewModel-->>View: Property change displays the definitions

    Reader->>View: Select a definition and click Save selected meaning
    View->>Controller: saveMeaning(selectedIndex)
    Controller->>Interactor: saveMeaning(selectedIndex)
    Interactor->>Storage: saveCard(card)
    alt New meaning saved
        Storage-->>Interactor: Newly saved card
        Interactor->>Presenter: prepareSavedView(meaning, false)
    else Meaning already in deck
        Storage-->>Interactor: Existing card with its last rating
        Interactor->>Presenter: prepareSavedView(meaning, true)
    else Deck cannot be read or written
        Storage-->>Interactor: IOException
        Interactor->>Presenter: prepareSaveFailView(error)
    end
    Presenter->>ViewModel: showMessage(status)
    ViewModel-->>View: Property change updates the status
```

The selected row identifies one word, part of speech and definition. A new meaning is written to `deck.json` before it is added to the in-memory deck. If saving fails, the selected row stays in place so the reader can try again.
