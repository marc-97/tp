package seedu.flashback.logic.parser;

import static seedu.flashback.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.flashback.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.flashback.logic.commands.AddCommand;
import seedu.flashback.logic.commands.AliasCommand;
import seedu.flashback.logic.commands.ClearCommand;
import seedu.flashback.logic.commands.Command;
import seedu.flashback.logic.commands.DeleteCommand;
import seedu.flashback.logic.commands.EditCommand;
import seedu.flashback.logic.commands.ExitCommand;
import seedu.flashback.logic.commands.FilterCommand;
import seedu.flashback.logic.commands.FindCommand;
import seedu.flashback.logic.commands.HelpCommand;
import seedu.flashback.logic.commands.ListCommand;
import seedu.flashback.logic.commands.RedoCommand;
import seedu.flashback.logic.commands.ReviewCommand;
import seedu.flashback.logic.commands.SortCommand;
import seedu.flashback.logic.commands.StatsCommand;
import seedu.flashback.logic.commands.UndoCommand;
import seedu.flashback.logic.commands.ViewCommand;
import seedu.flashback.logic.parser.exceptions.ParseException;
import seedu.flashback.model.Model;
import seedu.flashback.model.ModelManager;

/**
 * Parses user input.
 */
public class FlashBackParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private Model model = new ModelManager();
    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        String commandWord = matcher.group("commandWord");
        if (model.isAlias(commandWord)) {
            commandWord = model.parseAlias(commandWord);
        }
        final String arguments = matcher.group("arguments");
        switch (commandWord) {
        //case RemarkCommand.COMMAND_WORD: return new RemarkCommandParser().parse(arguments);
        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case ViewCommand.COMMAND_WORD:
            return new ViewCommandParser().parse(arguments);

        case ReviewCommand.COMMAND_WORD:
            return new ReviewCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);

        case StatsCommand.COMMAND_WORD:
            return new StatsCommandParser().parse(arguments);

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case AliasCommand.COMMAND_WORD:
            return new AliasCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
