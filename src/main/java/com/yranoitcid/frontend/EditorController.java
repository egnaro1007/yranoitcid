package com.yranoitcid.frontend;

import com.yranoitcid.backend.dictionary.Dictionary;
import com.yranoitcid.backend.dictionary.Word;
import com.yranoitcid.backend.util.HTMLConverter;

import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditorController implements Initializable {
    @FXML
    private TextField wordInput;
    @FXML
    private TextField pronounInput;
    @FXML
    private TextArea descInput;
    @FXML
    private TextArea htmlInput;
    @FXML
    private TextField wordSearchInput;

    @FXML
    private ListView<String> wordList;

    Dictionary workingDictionary = new Dictionary("dict.db");
    ObservableList<String> wordListDisplay = FXCollections.observableArrayList();
    ArrayList<Word> putDataHere = new ArrayList<>();
    String keyword;

    public void initialize(URL arg0, ResourceBundle arg1) {
        // Init the dictionary (default is the English dictionary).
        try {
            workingDictionary.initTable("en", "vi", "av");
        } catch (RuntimeException e) {
            System.out.println("Error in initiating the dictionary.");
        }

        // Limiting character input.
        wordInput.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String characterTyped = event.getCharacter();

            // Check if the typed character is a valid character
            if (characterTyped.matches("[a-zA-Z0-9\\s!@#$%^&*()_+-]")
                    || event.getCode() == KeyCode.BACK_SPACE) {
                // Trigger your method here
                try {
                    // getKeyword();
                    System.out.println("Key typed: " + characterTyped);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Ignore invalid keys
                event.consume();
            }
        });
        System.out.println("Editor menu initialized successfully.");
    }

    /**
     * Display words list.
     * Whenever a word is chosen, data related to it will be displayed on the editable text fields.
     */
    public void fetchResult() throws Exception {
        keyword = wordSearchInput.getText();
        System.out.println(keyword);
        putDataHere.clear();
        wordListDisplay.clear();
        putDataHere = workingDictionary.searchContains("en", "vi", keyword);
        for (int i = 0; i < putDataHere.size(); i++) {
            wordListDisplay.add(putDataHere.get(i).getWord());
        }
        wordList.setItems(wordListDisplay);
        wordList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int id = wordList.getSelectionModel().getSelectedIndex();
            wordInput.setText(putDataHere.get(id).getWord());
            pronounInput.setText(putDataHere.get(id).getPronounce());
            descInput.setText(putDataHere.get(id).getDescription());
            htmlInput.setText(putDataHere.get(id).getHtml());
        });
    }

    /**
     * Remove the selected word from the database. Currently, this is a permanent deletion
     * and the only way to bring back the word is replacing the current one with backups.
     */
    public void removeWordFromDatabase() throws Exception {
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
        if (Objects.equals(workingDictionary.searchExact(
            "en",
            "vi",
            wordInput.getText()
        ).getWord(), wordInput.getText())) {
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



    /**
     * Convert texts from the text-to-HTML field to HTML and put it in the HTML field.
     * If it fails, create a warning popup.
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

    /**
     * For testing purpose.
     */
    public void getTextInField() {
        String res = htmlInput.getText();
        System.out.println(res);
    }
}
