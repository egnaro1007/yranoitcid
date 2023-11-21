package com.yranoitcid.frontend;

import com.yranoitcid.backend.dictionary.Dictionary;
import com.yranoitcid.backend.dictionary.Word;
import com.yranoitcid.backend.dictionary.WordEdit;
import com.yranoitcid.backend.minigame.MultipleChoiceQuestion;
import com.yranoitcid.backend.minigame.MultipleChoices;
import com.yranoitcid.backend.util.HTMLConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;

import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class EditorController implements Initializable {

    private final MultipleChoices multipleChoices = MultipleChoices.getInstance();

    @FXML
    private TabPane settingsPane;
    @FXML
    private TextField wordInput;
    @FXML
    private TextField pronounInput;
    @FXML
    private TextField descInput;
    @FXML
    private TextArea textInput;
    @FXML
    private Tooltip textInputHelp;
    @FXML
    private TextArea htmlInput;
    @FXML
    private Tooltip htmlInputHelp;
    @FXML
    private TextField wordSearchInput;
    @FXML
    private ListView<String> wordList;
    @FXML
    private Button browseButton;
    @FXML
    private ListView<String> questionsList;

    Dictionary workingDictionary = Dictionary.getInstance("dict.db");
    ObservableList<String> wordListDisplay = FXCollections.observableArrayList();
    ArrayList<Word> putDataHere = new ArrayList<>();
    String keyword;
    WordEdit wordEdit = new WordEdit();

    @FXML
    private HBox wordEditorPane;
    private Node textMode;
    private Node htmlMode;

    public void initialize(URL arg0, ResourceBundle arg1) {
        // Get modes menu
        textMode = wordEditorPane.getChildren().get(0);
        htmlMode = wordEditorPane.getChildren().get(1);

        wordEditorPane.getChildren().clear();
        wordEditorPane.getChildren().add(textMode);

        // Load the questions list of the multiple choices game.
        refreshQuestionsList();

        // Init the dictionary (default is the English dictionary).
        try {
            workingDictionary.initTable("en", "vi", "av");
        } catch (RuntimeException e) {
            System.out.println("Error in initiating the dictionary.");
        }

        for (Tab tab : settingsPane.getTabs()) {
            tab.setClosable(false);
        }
        settingsPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Tab tab : c.getAddedSubList()) {
                            tab.setClosable(false);
                        }
                    }
                }
            }
        });

        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(null);

                try (InputStream quizFile = new FileInputStream(selectedFile)) {
                    multipleChoices.updateDatabase(quizFile);
                    refreshQuestionsList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        textInputHelp.setText("Can it\ndo this?\nnó nói tiếng vệt được không?");
        htmlInputHelp.setText("Can it?????\nhehe");

        System.out.println("Editor menu initialized successfully.");
    }

    /**
     * Get the keyword from text field immediately after input is detected and fetch results
     * directly into the list.
     */
    @FXML
    private void getKeyword() {
        keyword = wordSearchInput.getText();
        Task<Void> editorTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Dictionary called.");
                putDataHere = workingDictionary.searchContains("en", "vi", keyword);
                return null;
            }
        };
        editorTask.setOnSucceeded(workerStateEvent -> {
            try {
                fetchResult();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread editorThread = new Thread(editorTask);
        editorThread.start();
        System.out.println(keyword);
    }

    /**
     * Display words list. Whenever a word is chosen, data related to it will be displayed on the
     * editable text fields.
     */
    private void fetchResult() throws Exception {
        System.out.println("Editor list called.");
        wordListDisplay.clear();
        for (Word word : putDataHere) {
            wordListDisplay.add(word.getWord());
        }
        wordList.setItems(wordListDisplay);
        wordList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    int id = wordList.getSelectionModel().getSelectedIndex();

                    wordEdit.loadWord(putDataHere.get(id));

                    wordInput.setText(wordEdit.getWord());
                    pronounInput.setText(wordEdit.getPronounce());
                    descInput.setText(wordEdit.getDescription());

                    textInput.setText(wordEdit.getText());
                    htmlInput.setText(wordEdit.getHtml());
                });
    }

    /**
     * Remove the selected word from the database. Currently, this is a permanent deletion and the
     * only way to bring back the word is replacing the current one with backups.
     */
    public void removeWordFromDatabase() throws Exception {
        System.out.println("Proceeding to remove: " + wordInput.getText());
        // If word is blank or doesn't exist, abort.
        if (wordInput.getText().isBlank()
                || workingDictionary.searchExact("en", "vi", wordInput.getText()) == null) {
            Alert warning = new Alert(AlertType.ERROR);
            warning.setTitle("Invalid Input");
            warning.setHeaderText("Cannot delete word");
            if (warning.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
                return;
            }
        }

        // Confirmation.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove word");
        alert.setHeaderText("Are you sure you want to delete the word from the database?");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Remove word.
            workingDictionary.removeWord("en", "vi", wordInput.getText());
            wordInput.setText(wordListDisplay.get(0));
            fetchResult();
        }
    }

    /**
     * Change the information of chosen word.
     */
    public void changeWord() throws Exception {
        System.out.println("Proceeding to update: " + wordInput.getText());
        // If no input is detected, abort.
        if (wordInput.getText().isBlank()
                || pronounInput.getText().isBlank()
                || htmlInput.getText().isBlank()) {
            Alert warning = new Alert(AlertType.ERROR);
            warning.setTitle("Invalid Input");
            warning.setHeaderText("No input detected");
            if (warning.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
                return;
            }
        }

        // If word doesn't exist, abort.
        if (workingDictionary.searchExact("en", "vi", wordInput.getText()) == null) {
            Alert warning = new Alert(AlertType.ERROR);
            warning.setTitle("Invalid Input");
            warning.setHeaderText("Word doesn't exist");
            if (warning.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
                return;
            }
        }

        // Confirmation.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update word");
        alert.setHeaderText("Are you sure you want to update word from the database?");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            workingDictionary.removeWord("en", "vi", wordInput.getText());
            Word newWord = new Word(
                    wordInput.getText(),
                    htmlInput.getText(),
                    descInput.getText(),
                    pronounInput.getText()
            );
            workingDictionary.addWord("en", "vi", newWord);
        }
    }

    /**
     * Add a new word to the database.
     */
    public void addWordToDatabase() throws Exception {
        System.out.println("Proceeding to add: " + wordInput.getText());
        // If no input is detected, abort.
        if (wordInput.getText().isBlank()
                || pronounInput.getText().isBlank()
                || htmlInput.getText().isBlank()) {
            Alert warning = new Alert(AlertType.ERROR);
            warning.setTitle("Invalid Input");
            warning.setHeaderText("No input detected");
            if (warning.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
                return;
            }
        }

        // If word did exist, abort.
        if (workingDictionary.searchExact(
                "en",
                "vi",
                wordInput.getText()
        ) != null) {
            Alert warning = new Alert(AlertType.ERROR);
            warning.setTitle("Invalid Input");
            warning.setHeaderText("Word already exist");
            if (warning.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
                return;
            }
        }

        // Confirmation.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add word");
        alert.setHeaderText("Are you sure you want to add this word to the database?");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Word newWord = new Word(
                    wordInput.getText(),
                    htmlInput.getText(),
                    descInput.getText(),
                    pronounInput.getText()
            );
            workingDictionary.addWord("en", "vi", newWord);
        }
    }

    @FXML
    private void editInWordField() {
        String newWord = wordInput.getText();
        wordEdit.setWord(newWord);
        textInput.setText(wordEdit.getText());
        htmlInput.setText(wordEdit.getHtml());
    }

    @FXML
    private void editInPronounField() {
        String newPronoun = pronounInput.getText();
        wordEdit.setPronounce(newPronoun);
        textInput.setText(wordEdit.getText());
        htmlInput.setText(wordEdit.getHtml());
    }

    @FXML
    private void editInTextField() {
        String newText = textInput.getText();
        try {
            wordEdit.setText(newText);

            wordInput.setText(wordEdit.getWord());
            pronounInput.setText(wordEdit.getPronounce());
            htmlInput.setText(wordEdit.getHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editInHtmlField() {
        String newHtml = htmlInput.getText();
        wordEdit.setHtml(newHtml);

        textInput.setText(wordEdit.getText());
        wordInput.setText(wordEdit.getWord());
        pronounInput.setText(wordEdit.getPronounce());
    }

    private void refreshQuestionsList() {
        int i = 0;
        questionsList.getItems().clear();
        for (String question : multipleChoices.getQuestionsSet()) {
            i++;
            questionsList.getItems().add(String.format("%3d.  %s", i, question));
        }
    }


    /**
     * Convert texts from the text-to-HTML field to HTML and put it in the HTML field. If it fails,
     * create a warning popup.
     */
    public void convert() throws IOException {
        System.out.println(htmlInput.getText());
        try {
            String temp = HTMLConverter.stringToHtml(htmlInput.getText());
            htmlInput.setText(temp);
        } catch (Exception e) {
            System.out.println("Bruh");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("There are invalid texts in the box.");
            if (alert.showAndWait().get() == ButtonType.OK) {
                System.out.println("Hah");
            }
        }
    }

    public void switchToHtmlMode() {
        wordEditorPane.getChildren().set(0, htmlMode);
    }

    public void switchToTextMode() {
        wordEditorPane.getChildren().set(0, textMode);
    }

    /**
     * For testing purpose.
     */
    public void getTextInField() {
        String res = htmlInput.getText();
        System.out.println(res);
    }
}
