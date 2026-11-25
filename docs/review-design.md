# Review saved words: design

Each box in the class diagram is one of our packages, and the packages match the Clean Architecture layers. `entity` is the innermost layer, then `use_case`, then `interface_adapter`, with `view` and `data_access` on the outside. No arrow goes from an inner layer to an outer one. `ReviewInteractor` only uses the entities and the interfaces in `use_case`, so it doesn't depend on Swing or on how the deck is stored. `app.Main` creates the objects and connects them, so it isn't shown.

## Class diagram

```mermaid
classDiagram
    direction LR

    namespace view {
        class ReviewView
    }

    namespace interface_adapter {
        class ReviewController {
            +startReview()
            +rateCard(boolean gotIt)
            +endReview()
        }
        class ReviewPresenter
        class ReviewViewModel
        class ReviewState
    }

    namespace use_case {
        class ReviewInputBoundary {
            <<interface>>
            +startReview()
            +rateCard(ReviewInputData inputData)
            +endReview()
        }
        class ReviewInputData
        class ReviewInteractor {
            -ReviewSession session
        }
        class ReviewOutputBoundary {
            <<interface>>
            +prepareCardView(ReviewOutputData card)
            +prepareSummaryView(ReviewSummaryOutputData summary)
            +prepareFailView(String error)
        }
        class ReviewOutputData
        class ReviewSummaryOutputData
        class ReviewDataAccessInterface {
            <<interface>>
            +getDeck() Deck
            +saveRating(Flashcard card, String rating)
        }
    }

    namespace data_access {
        class FileDeckDataAccessObject
    }

    namespace entity {
        class Deck {
            +cardsForReview() List~Flashcard~
        }
        class Flashcard {
            +recordRating(String rating)
        }
        class ReviewSession {
            +currentCard() Flashcard
            +advance(String rating)
            +isFinished() boolean
        }
    }

    ReviewView --> ReviewController
    ReviewView --> ReviewViewModel
    ReviewController ..> ReviewInputData
    ReviewController --> ReviewInputBoundary
    ReviewInteractor ..|> ReviewInputBoundary
    ReviewInteractor --> ReviewOutputBoundary
    ReviewInteractor --> ReviewDataAccessInterface
    ReviewInteractor --> ReviewSession
    ReviewInteractor ..> ReviewOutputData
    ReviewInteractor ..> ReviewSummaryOutputData
    ReviewPresenter ..|> ReviewOutputBoundary
    ReviewPresenter --> ReviewViewModel
    ReviewViewModel --> ReviewState
    FileDeckDataAccessObject ..|> ReviewDataAccessInterface
    FileDeckDataAccessObject --> Deck
    Deck o-- Flashcard
    ReviewSession o-- Flashcard
```

## Rating a card

This is what happens when the learner clicks *Got it*. *Again* is the same with the other rating.

```mermaid
sequenceDiagram
    actor Learner
    participant View as ReviewView
    participant Controller as ReviewController
    participant Interactor as ReviewInteractor
    participant Storage as FileDeckDataAccessObject
    participant Card as Flashcard
    participant Session as ReviewSession
    participant Presenter as ReviewPresenter
    participant ViewModel as ReviewViewModel

    Learner->>View: clicks Got it
    View->>Controller: rateCard(true)
    Controller->>Interactor: rateCard(ReviewInputData)
    Interactor->>Storage: saveRating(card, "Got it")
    alt rating saved
        Interactor->>Card: recordRating("Got it")
        Interactor->>Session: advance("Got it")
        alt more cards left
            Interactor->>Presenter: prepareCardView(ReviewOutputData)
        else that was the last card
            Interactor->>Presenter: prepareSummaryView(ReviewSummaryOutputData)
        end
    else saving failed
        Interactor->>Presenter: prepareFailView("Could not save the rating. Please try again.")
    end
    Presenter->>ViewModel: update the state, firePropertyChanged()
    ViewModel->>View: propertyChange(event)
    View->>View: updateView()
```

The rating is saved before it is recorded on the card or counted. If the save fails, the learner stays on the same card and the totals don't change, which is extension 3a of the use case.
