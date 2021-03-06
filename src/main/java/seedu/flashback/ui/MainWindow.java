package seedu.flashback.ui;

import static seedu.flashback.ui.ReviewMode.EXIT_REVIEW_MODE;

import java.util.Optional;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.flashback.commons.core.GuiSettings;
import seedu.flashback.commons.core.LogsCenter;
import seedu.flashback.commons.core.index.Index;
import seedu.flashback.logic.Logic;
import seedu.flashback.logic.commands.CommandResult;
import seedu.flashback.logic.commands.exceptions.CommandException;
import seedu.flashback.logic.parser.exceptions.ParseException;
import seedu.flashback.model.flashcard.Flashcard;
import seedu.flashback.model.flashcard.Question;
import seedu.flashback.model.flashcard.Statistics;


/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private FlashcardListPanel flashcardListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane flashcardListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane flashcardViewCardPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private GridPane commandModePane;

    @FXML
    private StackPane reviewModePlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        flashcardListPanel = new FlashcardListPanel(logic.getFilteredFlashcardList());
        flashcardListPanelPlaceholder.getChildren().add(flashcardListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getFlashBackFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        commandModePane.managedProperty().bind(commandModePane.visibleProperty());

        reviewModePlaceholder.setVisible(false);
        reviewModePlaceholder.managedProperty().bind(reviewModePlaceholder.visibleProperty());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    private void handleView(int index) {
        clearViewArea();
        FlashbackViewCard flashbackViewCard = new FlashbackViewCard(logic.getFilteredFlashcardList().get(index));
        flashcardViewCardPlaceholder.getChildren().add(flashbackViewCard.getRoot());
    }

    /**
     * Handles the case when the user has requested to view flashcard(s) statistics.
     *
     * @param stats Statistics of the flashcard(s).
     * @param statsIndex Index of the flashcard, if any.
     */
    private void handleStats(Statistics stats, Optional<Index> statsIndex) {
        clearViewArea();
        Optional<Question> question = Optional.empty();
        if (statsIndex.isPresent()) {
            int idx = statsIndex.get().getZeroBased();
            assert(idx >= 0);
            Flashcard flashcard = logic.getFilteredFlashcardList().get(idx);
            question = Optional.of(flashcard.getQuestion());
        }
        FlashbackStats flashbackStats = new FlashbackStats(stats, question);
        flashcardViewCardPlaceholder.getChildren().add(flashbackStats.getRoot());
    }

    private void enterReviewMode(ReviewMode reviewMode) {
        commandModePane.setVisible(false);
        commandBoxPlaceholder.setVisible(false);
        reviewModePlaceholder.getChildren().add(reviewMode.getRoot());
        reviewModePlaceholder.setVisible(true);
    }

    protected void exitReviewMode() {
        commandModePane.setVisible(true);
        commandBoxPlaceholder.setVisible(true);
        reviewModePlaceholder.setVisible(false);
        reviewModePlaceholder.getChildren().clear();
        resultDisplay.setFeedbackToUser(EXIT_REVIEW_MODE);
    }

    private void clearViewArea() {
        flashcardViewCardPlaceholder.getChildren().clear();
    }

    public FlashcardListPanel getFlashcardListPanel() {
        return flashcardListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.flashback.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            clearViewArea();

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            if (commandResult.isShowView()) {
                handleView(commandResult.getIndex());
            }

            if (commandResult.isReviewMode()) {
                enterReviewMode(new ReviewMode(logic, this));
            }

            if (commandResult.isShowStats()) {
                handleStats(commandResult.getStats(), commandResult.getStatsIndex());
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
