package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ANSWER_DESC_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.ANSWER_DESC_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ANSWER_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CATEGORY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRIORITY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_QUESTION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.PRIORITY_DESC_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.PRIORITY_DESC_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.QUESTION_DESC_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_EQUATION;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_GENERAL;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ANSWER_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ANSWER_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CATEGORY_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CATEGORY_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRIORITY_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRIORITY_OCTOPUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_QUESTION_EINSTEIN;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_EQUATION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GENERAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FLASHCARD;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FLASHCARD;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_FLASHCARD;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditFlashcardDescriptor;
import seedu.address.model.flashcard.Answer;
import seedu.address.model.flashcard.Category;
import seedu.address.model.flashcard.Priority;
import seedu.address.model.flashcard.Question;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditFlashcardDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_QUESTION_EINSTEIN, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + QUESTION_DESC_EINSTEIN, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + QUESTION_DESC_EINSTEIN, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_QUESTION_DESC, Question.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_ANSWER_DESC, Answer.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_CATEGORY_DESC, Category.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_PRIORITY_DESC, Priority.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_ANSWER_DESC + CATEGORY_DESC_EINSTEIN, Answer.MESSAGE_CONSTRAINTS);

        // valid phone followed by invalid phone. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        assertParseFailure(parser, "1" + ANSWER_DESC_OCTOPUS + INVALID_ANSWER_DESC, Answer.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_GENERAL + TAG_DESC_EQUATION
                + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_GENERAL + TAG_EMPTY
                + TAG_DESC_EQUATION, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_GENERAL
                + TAG_DESC_EQUATION, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_QUESTION_DESC + INVALID_CATEGORY_DESC
                        + VALID_PRIORITY_EINSTEIN + VALID_ANSWER_EINSTEIN,
                Question.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_FLASHCARD;
        String userInput = targetIndex.getOneBased() + ANSWER_DESC_OCTOPUS + TAG_DESC_EQUATION
                + CATEGORY_DESC_EINSTEIN + PRIORITY_DESC_EINSTEIN + QUESTION_DESC_EINSTEIN + TAG_DESC_GENERAL;

        EditCommand.EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder()
                .withQuestion(VALID_QUESTION_EINSTEIN)
                .withAnswer(VALID_ANSWER_OCTOPUS).withCategory(VALID_CATEGORY_EINSTEIN)
                .withPriority(VALID_PRIORITY_EINSTEIN)
                .withTags(VALID_TAG_EQUATION, VALID_TAG_GENERAL).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_FLASHCARD;
        String userInput = targetIndex.getOneBased() + ANSWER_DESC_OCTOPUS + CATEGORY_DESC_EINSTEIN;

        EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder().withAnswer(VALID_ANSWER_OCTOPUS)
                .withCategory(VALID_CATEGORY_EINSTEIN).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_FLASHCARD;
        String userInput = targetIndex.getOneBased() + QUESTION_DESC_EINSTEIN;
        EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder()
                .withQuestion(VALID_QUESTION_EINSTEIN).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + ANSWER_DESC_EINSTEIN;
        descriptor = new EditFlashcardDescriptorBuilder().withAnswer(VALID_ANSWER_EINSTEIN).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + CATEGORY_DESC_EINSTEIN;
        descriptor = new EditFlashcardDescriptorBuilder().withCategory(VALID_CATEGORY_EINSTEIN).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + PRIORITY_DESC_EINSTEIN;
        descriptor = new EditFlashcardDescriptorBuilder().withPriority(VALID_PRIORITY_EINSTEIN).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_GENERAL;
        descriptor = new EditFlashcardDescriptorBuilder().withTags(VALID_TAG_GENERAL).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_FLASHCARD;
        String userInput = targetIndex.getOneBased() + ANSWER_DESC_EINSTEIN + PRIORITY_DESC_EINSTEIN
                + CATEGORY_DESC_EINSTEIN + TAG_DESC_GENERAL + ANSWER_DESC_EINSTEIN
                + PRIORITY_DESC_EINSTEIN + CATEGORY_DESC_EINSTEIN + TAG_DESC_GENERAL
                + ANSWER_DESC_OCTOPUS + PRIORITY_DESC_OCTOPUS + CATEGORY_DESC_OCTOPUS + TAG_DESC_EQUATION;

        EditCommand.EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder()
                .withAnswer(VALID_ANSWER_OCTOPUS).withCategory(VALID_CATEGORY_OCTOPUS)
                .withPriority(VALID_PRIORITY_OCTOPUS).withTags(VALID_TAG_GENERAL, VALID_TAG_EQUATION)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_FLASHCARD;
        String userInput = targetIndex.getOneBased() + INVALID_ANSWER_DESC + ANSWER_DESC_OCTOPUS;
        EditCommand.EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder()
                .withAnswer(VALID_ANSWER_OCTOPUS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + CATEGORY_DESC_OCTOPUS + INVALID_ANSWER_DESC + PRIORITY_DESC_OCTOPUS
                + ANSWER_DESC_OCTOPUS;
        descriptor = new EditFlashcardDescriptorBuilder().withAnswer(VALID_ANSWER_OCTOPUS)
                .withCategory(VALID_CATEGORY_OCTOPUS).withPriority(VALID_PRIORITY_OCTOPUS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_FLASHCARD;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditFlashcardDescriptor descriptor = new EditFlashcardDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
