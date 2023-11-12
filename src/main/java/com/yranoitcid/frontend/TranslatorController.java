package com.yranoitcid.frontend;

import com.yranoitcid.backend.api.GoogleChan;

import com.yranoitcid.backend.dictionary.Word;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.yranoitcid.backend.api.GoogleChanTTS;
import com.yranoitcid.backend.util.ClipboardAccess;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class TranslatorController implements Initializable{
    
    @FXML
    private TextArea translateInput;
    @FXML
    private Label resultTranslate;
    private String result;
    private Media audio;
    private MediaPlayer mediaPlayer;
    private boolean readyToSay = false;
    @FXML
    private Button search;
    @FXML
    private ProgressBar loadingBar;
    @FXML
    private ChoiceBox<String> languageSelectSrc;
    @FXML
    private ChoiceBox<String> languageSelectDes;

    GoogleChan グーグルちゃん = new GoogleChan();
    GoogleChanTTS guuguruChan = new GoogleChanTTS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        languageSelectSrc.setItems(FXCollections.observableArrayList("English (en)", "Vietnamese (vi)"));
        languageSelectSrc.setValue("English (en)");

        languageSelectDes.setItems(FXCollections.observableArrayList("English (en)", "Vietnamese (vi)"));
        languageSelectDes.setValue("Vietnamese (vi)");

        System.out.println("Translator created successfully!");
    }

    /**
     * Translate input in the input text area and display them on the result box.
     */
    public void translate() {
        // Get input.
        String text = translateInput.getText();
        String langSrc = languageSelectSrc.getValue();
        String langDes = languageSelectDes.getValue();

        // Verify input is not empty.
        if (text.trim().isEmpty()) {
            resultTranslate.setText("");
            return;
        }

        // Show loading bar.
        loadingBar.setVisible(true);

        // Input processing.
        グーグルちゃん.setLanguage(langSrc.substring(langSrc.length() - 3, langSrc.length() - 1),
            langDes.substring(langDes.length() - 3, langDes.length() - 1));

        guuguruChan.setLanguage(langDes.substring(langDes.length() - 3, langDes.length() - 1));
        System.out.println(text);

        readyToSay = false;
        resultTranslate.setText("Translating...");

        // Prepare audio.
        Task<Void> prepareAudioTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                audio = guuguruChan.say(result);
                mediaPlayer = new MediaPlayer(audio);
                mediaPlayer.setOnReady(() -> {
                    readyToSay = true;
                    loadingBar.setVisible(false);
                });
                return null;
            }
        };

        // Translate.
        Task<Void> translateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Translate the text
                Word resultWord = グーグルちゃん.search(text);
                if (resultWord == null) {
                    connectionFail();
                    return null;
                }
                System.out.println("Result: " + resultWord);
                result = resultWord.getDescription();

                // Start preparing the audio
                Thread prepareAudioThread = new Thread(prepareAudioTask);
                prepareAudioThread.start();

                return null;
            }
        };
        translateTask.setOnSucceeded(workerStateEvent -> {
            resultTranslate.setText(result);
        });


        Task<Void> countDownTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }
        };
        countDownTask.setOnSucceeded(workerStateEvent -> {
            if (!readyToSay) {
                connectionFail();
            }
        });

        Thread translatorThread = new Thread(translateTask);
        Thread countDownThread = new Thread(countDownTask);
        translatorThread.start();
        countDownThread.start();
    }

    private void connectionFail() {
        loadingBar.setVisible(false);
        resultTranslate.setText("");
        readyToSay = true;
        System.out.println("Connection fail.");
    }

    /**
     * Paste text from clipboard to the input text area.
     */
    public void paste() {
        translateInput.setText(ClipboardAccess.getString());
    }

    /**
     * Play audio taken from the result box.
     */
    public void playAudio()  {
        String langDes = languageSelectDes.getValue();
        guuguruChan.setLanguage(langDes.substring(langDes.length() - 3, langDes.length() - 1));
        Task<Void> guuguruChanTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                guuguruChan.say(result);
//                MediaPlayer mediaPlayer = new MediaPlayer(audio);
//                mediaPlayer.setOnReady(mediaPlayer::play);
                if (readyToSay) {
                    mediaPlayer.stop();
                    mediaPlayer.play();
                } else {
                    mediaPlayer.setOnReady(() -> {
                        readyToSay = true;
                        loadingBar.setVisible(false);
                        mediaPlayer.play();
                    });
                }
                return null;
            }
        };
        Thread guuguruChanThread = new Thread(guuguruChanTask);
        guuguruChanThread.start();
    }
}
