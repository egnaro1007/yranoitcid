package com.yranoitcid.frontend;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.yranoitcid.backend.dictionary.*;
import com.yranoitcid.backend.api.GoogleChanTTS;

import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

public class DictionaryController implements Initializable {

    // Listing all the elements in the current menu.
    @FXML
    private ListView<String> resultList;
    @FXML
    private WebView resultHtml;
    ArrayList<Word> putDataHere = new ArrayList<>();
    ObservableList<String> resultListDisplay = FXCollections.observableArrayList();

    @FXML
    private TextField searchInput;
    String keyword;
    String resultWord;

    // Dictionary
    Dictionary workingDictionary = new Dictionary("dict.db");
    GoogleChanTTS guuguruChan = new GoogleChanTTS();


    /**
     * Initialize the window. This will include the dictionary and search option choice box.
     * Called every time the app starts.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
            workingDictionary.initTable("en", "vi", "av");
        } catch (RuntimeException e) {
            System.out.println("Error in initiating the dictionary.");
        }

        // String normalization
        searchInput.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String characterTyped = event.getCharacter();

            // Check if the typed character is a valid character
            if (characterTyped.matches("[a-zA-Z0-9\\s!@#$%^&*()_+-]")
                    || event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
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

        System.out.println("dictionary window created successfully!");
    }

    /**
     * Get the keyword from text field immediately after input is detected and fetch results
     * directly into the list.
     */
    public void getKeyword() throws ExecutionException, InterruptedException {
        keyword = searchInput.getText();
        Task<Void> dictionaryTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Dictionary called.");
                putDataHere = workingDictionary.searchContains("en", "vi", keyword);
                return null;
            }
        };
        dictionaryTask.setOnSucceeded(workerStateEvent -> {
            try {
                fetchResult();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread dictionaryThread = new Thread(dictionaryTask);
        dictionaryThread.start();
        System.out.println("Text: " + keyword);
    }


    public void fetchResult() throws Exception {
        System.out.println("Dictionary running.");
        resultListDisplay.clear();
          for (Word word : putDataHere) {
            resultListDisplay.add(word.getWord());
          }
        resultList.setItems(resultListDisplay);
        resultList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                int id = resultList.getSelectionModel().getSelectedIndex();
                resultWord = putDataHere.get(id).getWord();
                resultHtml.getEngine().loadContent(putDataHere.get(id).getHtml());
            });
    }

    /**
     * Play the pronunciation of the chosen word.
     */
    public void playAudio() {
        Task<Void> guuguruChanTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                guuguruChan.say(resultWord);
                return null;
            }
        };
        guuguruChanTask.setOnSucceeded(workerStateEvent -> {
            System.out.println("Guuguru-chan said something!");
        });
        Thread guuguruChanThread = new Thread(guuguruChanTask);
        guuguruChanThread.start();
    }

}