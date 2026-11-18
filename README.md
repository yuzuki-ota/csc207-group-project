# WordDeck

WordDeck is a Java Swing app for learning English vocabulary. A reader can look up a word, save the meaning that fits the passage, and review it later. Cards and review ratings are stored in one local JSON file so the deck can be reopened and studied offline.

## MVP and user stories

| Lead | User story | Use case |
| --- | --- | --- |
| Yuzuki Ota | As a reader, I want to save the meaning of a word as it is used in my reading so I can recall it later. | Look up and save a word |
| Pranav | As a learner, I want to practise words I struggled with first so I can use my study time on them. | Review saved words |

Each lead will implement their use case from its Swing controls through the controller, interactor, and presenter. The two use cases share the deck and its storage interface.

## Use cases

### 1. Look up and save a word

**Primary actor** — Reader. **Precondition** — A deck is open, possibly empty. **Trigger** — The reader requests a lookup. **Success outcome** — The selected meaning is available as a card after the app restarts.

1. The reader enters an English word.
2. The app displays the dictionary's definitions and the part of speech for each one.
3. The reader chooses a definition and saves it.
4. The app adds the card to the deck and shows that it was saved.

**Extensions**

- **2a.** An empty search, an unknown word, or a failed connection produces a message and the reader can try again.
- **3a.** If that word, part of speech, and meaning are already saved, the app shows the existing card. A different meaning can be saved separately.
- **4a.** If storage fails, the app keeps the selection available for retry.

### 2. Review saved words

**Primary actor** — Learner. **Precondition** — The deck contains a card. **Trigger** — The learner starts a review. **Success outcome** — The learner sees the session totals, and their saved ratings determine the next review order.

1. The app shows one word at a time, starting with cards last marked *Again*. Cards in the same group keep their original deck order.
2. The learner recalls the meaning, reveals the definition, and chooses *Got it* or *Again*.
3. The app saves the rating and moves to the next card. Each card appears once per session.
4. After the last card, the app shows how many cards received each rating.

**Extensions**

- **1a.** If the deck is empty, the app prompts the learner to add a word first.
- **3a.** If a rating cannot be saved, the app stays on the card and allows a retry. The total changes only after a successful save.
- **4a.** If the learner ends early, the app shows totals for the cards already rated.

## Entities

| Entity | Instance variables | Methods |
| --- | --- | --- |
| `Flashcard` | `word`, `partOfSpeech`, `definition`, `lastRating` (New, Again, or Got it) | `matchesMeaning(other)` compares the word, part of speech, and definition. `recordRating(rating)` updates the last rating. |
| `Deck` | `cards`, in the order added | `addCard(card)` checks for a duplicate before adding. `cardsForReview()` returns the review order. |
| `ReviewSession` | `orderedCards`, `currentIndex`, `gotItCount`, `againCount` | `currentCard()` returns the prompt. `advance(rating)` counts a completed rating and moves forward. `isFinished()` checks for the end of the deck. |

## API usage

The lookup uses the [Free Dictionary API](https://dictionaryapi.dev/). A GET request for the word returns definitions grouped by part of speech. WordDeck will read the response with OkHttp and `org.json`, then save the learner's selected definition. A [browser lookup for *hello*](https://api.dictionaryapi.dev/api/v2/entries/en/hello) returned noun, verb, and interjection entries. Java integration is planned.

## Implementation order

First, agree on the deck file format using one sample card. Yuzuki can then build the lookup and save flow while Pranav builds the review flow using that sample deck. To check that the two flows work together, save two words, restart the app, mark one *Again*, and confirm it appears first in the next review.

## Current progress

`Flashcard`, deck insertion and duplicate checks, review ordering, `ReviewSession`, and the lookup interactor are implemented. The lookup screen is a layout only; its buttons are disabled. The controller, presenter, dictionary API connection, and file storage are still to do. The review UI and interactor are not included yet.

Open `pom.xml` as a Maven project and run `app.Main` to view the layout. Run `mvn test` to check the entities and lookup interactor.
