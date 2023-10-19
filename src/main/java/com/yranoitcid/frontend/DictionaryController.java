package com.yranoitcid.frontend;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.yranoitcid.backend.dictionary.*;
import com.yranoitcid.backend.api.GoogleChanTTS;

import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    Stage stage;

    Dictionary workingDictionary = new Dictionary("dict.db");
    GoogleChanTTS guuguruChan = new GoogleChanTTS();


    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private Long lastTime = System.currentTimeMillis();
    private final AtomicInteger pendingCalls = new AtomicInteger(0);
    private static final Long cd = 500L;

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
    public void getKeyword() {
        keyword = searchInput.getText();
        // fetchResult(searchModeChoice);
        delayedMethod(cd);
        System.out.println("Text: " + keyword);
    }

    /**
     * Call this method to start the timer.
     * The timer will count down for period of time.
     * While the timer is counting, if the method is called again the timer will be reset.
     * After counting is finish, execute method doWork()
     *
     * @param period Input the period of timer in millisecond
     * @see DictionaryController#doWork()
     */
    public void delayedMethod(Long period) {
        pendingCalls.incrementAndGet();

        long currentTime = System.currentTimeMillis();
        long timeSinceLastExecution = currentTime - lastTime;

        if (timeSinceLastExecution >= period) {
            executor.schedule(() -> {
                int remainingCalls = pendingCalls.decrementAndGet();
                if (remainingCalls == 0) {
                    Platform.runLater(this::doWork);
                }
            }, period, TimeUnit.MILLISECONDS);
        } else {
            long delay = period - timeSinceLastExecution;
            System.out.println("\u001B[33m" + "Delay" + "\u001B[0m");
            executor.schedule(() -> {
                int remainingCalls = pendingCalls.decrementAndGet();
                if (remainingCalls == 0) {
                    Platform.runLater(this::doWork);
                }
            }, delay, TimeUnit.MILLISECONDS);
        }

        lastTime = currentTime;
    }

    private void doWork() {
        // The code will be executed after the delay.
        try {
            fetchResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("\u001B[32m"
                        + "Method executed after " + cd + "ms of the last call."
                        + "\u001B[0m");
    }

    public void fetchResult() throws Exception {
        putDataHere.clear();
        resultListDisplay.clear();
        putDataHere = workingDictionary.searchContains("en", "vi", keyword);
        for (int i = 0; i < (Math.min(putDataHere.size(), 30)); i++) {
            resultListDisplay.add(putDataHere.get(i).getWord());
        }
        loadResult(resultListDisplay);
        resultList.getSelectionModel().selectedItemProperty().addListener(
                new WeakChangeListener<>((observable, oldValue, newValue) -> {
                    int id = resultList.getSelectionModel().getSelectedIndex();
                    resultWord = putDataHere.get(id).getWord();
                    resultHtml.getEngine().loadContent(putDataHere.get(id).getHtml());
                }));
    }

    public void playAudio() {
        guuguruChan.say(resultWord);
    }

    public void loadResult(ObservableList<String> resArray) {
        resultList.setItems(resArray);
    }

}