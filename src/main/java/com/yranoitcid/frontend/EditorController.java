package com.yranoitcid.frontend;

import com.yranoitcid.backend.dictionary.Dictionary;
import com.yranoitcid.backend.dictionary.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
        keyword = wordInput.getText();
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
        });
    }

    /**
     * Remove the selected word from the database. Currently, this is a permanent deletion
     * and the only way to bring back the word is replacing the current one with backups.
     */
    public void removeWordFromDatabase() throws Exception {
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
}
