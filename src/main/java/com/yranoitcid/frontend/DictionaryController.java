package com.yranoitcid.frontend;

import com.yranoitcid.backend.api.GoogleChanTTS;
import com.yranoitcid.backend.dictionary.Dictionary;
import com.yranoitcid.backend.dictionary.Word;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
    @FXML
    private ImageView audioButton;
    private final Image loading = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/image/wedges.gif")));
    private final Image loaded = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/image/Ichika08.png")));
    String keyword;
    String resultWord;

    // Dictionary thread.
    public class DictionaryThread extends Thread {
        ArrayList<Word> data = new ArrayList<>();
        boolean interrupted = false;
        Task<Void> dictionaryTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Dictionary called.");
                data = workingDictionary.searchContains("en", "vi", keyword);
                return null;
            }
        };
        public DictionaryThread() {
            dictionaryTask.setOnSucceeded(workerStateEvent -> {
                if (!interrupted) {
                    try {
                        putDataHere = data;
                        fetchResult();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        @Override
        public void run() {
            dictionaryTask.run();
        }

        public void setInterrupted() {
            interrupted = true;
        }
    }
    DictionaryThread dictionaryThread = null;

    // This boolean is used to check if the word is sayed for the first time.
    // Set to true when choose a word from resultList, set to false when playAudio() is called.
    private boolean newWord = true;
    private Media media;
    private MediaPlayer mediaPlayer;

    // Dictionary
    Dictionary workingDictionary = Dictionary.getInstance("dict.db");
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
//        searchInput.addEventHandler(KeyEvent.KEY_TYPED, event -> {
//            String characterTyped = event.getCharacter();
//
//            // Check if the typed character is a valid character
//            if (characterTyped.matches("[a-zA-Z0-9\\s!@#$%^&*()_+-]")
//                    || event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
//                // Trigger your method here
//                try {
//                    // getKeyword();
//                    System.out.println("Key typed: " + characterTyped);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            } else {
//                // Ignore invalid keys
//                event.consume();
//            }
//        });

        System.out.println("dictionary window created successfully!");
    }

    /**
     * Get the keyword from text field immediately after input is detected and fetch results
     * directly into the list.
     */
    public void getKeyword() throws ExecutionException, InterruptedException {
        keyword = searchInput.getText();

        if (dictionaryThread != null) {
            dictionaryThread.setInterrupted();
        }
        dictionaryThread = new DictionaryThread();
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

                    // Reset newWord to true when a new word is chosen.
                    newWord = true;
                });
    }

    public void chooseFirst() {
        choose(0);
    }

    public void choose(int id) {
        resultList.getSelectionModel().select(id);
        resultList.requestFocus();
    }

    /**
     * Play the pronunciation of the chosen word.
     */
    public void playAudio() {
        Task<Void> guuguruChanTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (newWord) {
                    audioButton.setImage(loading);
                    // Set to false when fetch audio completed.
                    newWord = false;
                    media = guuguruChan.say(resultWord);
//                    media = guuguruChan.parse();
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                    audioButton.setImage(loaded);
                        mediaPlayer.play();
                    });
                }
                mediaPlayer.stop();
                mediaPlayer.play();
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