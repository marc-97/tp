package seedu.flashback.logic.commands;

import static seedu.flashback.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.flashback.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.flashback.testutil.TypicalFlashcards.getTypicalFlashBack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.flashback.model.Model;
import seedu.flashback.model.ModelManager;
import seedu.flashback.model.UserPrefs;
import seedu.flashback.model.flashcard.Flashcard;
import seedu.flashback.testutil.FlashcardBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalFlashBack(), new UserPrefs());
    }

    @Test
    public void execute_newFlashcard_success() {
        Flashcard validFlashcard = new FlashcardBuilder().withQuestion("What animal cannot stick its tongue out?")
                .withAnswer("Crocodile").withCategory("Animals").withPriority("Low").build();

        Model expectedModel = new ModelManager(model.getFlashBack(), new UserPrefs());
        expectedModel.addFlashcard(validFlashcard);
        expectedModel.commitFlashBack();

        assertCommandSuccess(new AddCommand(validFlashcard), model,
                String.format(AddCommand.MESSAGE_SUCCESS, validFlashcard), expectedModel);
    }

    @Test
    public void execute_duplicateFlashcard_throwsCommandException() {
        Flashcard flashcardInList = model.getFlashBack().getCardList().get(0);
        assertCommandFailure(new AddCommand(flashcardInList), model, AddCommand.MESSAGE_DUPLICATE_FLASHCARD);
    }

}
