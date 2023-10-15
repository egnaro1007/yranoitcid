package com.yranoitcid.Frontend;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.yranoitcid.Backend.Dictionary.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class dictionaryController implements Initializable {

    @FXML
    private AnchorPane dictionaryPane;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> resultList;
    @FXML
    private Label resultWord;
    @FXML
    private Label resultPronun;
    @FXML
    private Label resultDesc;
    ArrayList<word> putDataHere = new ArrayList<>();
    ObservableList<String> resultListDisplay = FXCollections.observableArrayList();

    @FXML
    private TextField searchInput;
    String keyword;

    @FXML
    private ChoiceBox<String> searchModeChoiceBox;
    int searchModeChoice = 0;

    Stage stage;

    dictionary workingDictionary = new dictionary("dict.db");

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
        ObservableList<String> searchModeOptions = FXCollections.observableArrayList(
                "Contains...",
                "Full match"
        );
        searchModeChoiceBox.setItems(searchModeOptions);
        searchModeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    searchModeChoice = searchModeChoiceBox.getSelectionModel()
                            .getSelectedIndex();
                    System.out.println("Current mode: " + searchModeChoice);
                });

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

        System.out.println("Dictionary window created successfully!");
    }

    /**
     * Handles logout event.
     *
     * @param e
     */
    public void logout(ActionEvent e) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Really");
        alert.setHeaderText("Did you just fucking clicked the button??");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) dictionaryPane.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Get the keyword from text field immediately after input is detected and fetch results
     * directly into the list.
     *
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
     * @see dictionaryController#doWork()
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
            fetchResult(searchModeChoice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("\u001B[32m"
                        + "Method executed after " + cd + "ms of the last call."
                        + "\u001B[0m");
    }

    public void fetchResult(int searchModeChoice) throws Exception {
        switch (searchModeChoice) {
            case 0:
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
                            resultWord.setText(putDataHere.get(id).getWord());
                            resultPronun.setText(putDataHere.get(id).getPronounce());
                            resultDesc.setText(putDataHere.get(id).getDescription());
                        }));
                break;
            case 1:
                putDataHere.clear();
                resultListDisplay.clear();
                putDataHere.add(workingDictionary.searchExact("en", "vi", keyword));
                resultWord.setText(putDataHere.get(0).getWord());
                resultPronun.setText(putDataHere.get(0).getPronounce());
                resultDesc.setText(putDataHere.get(0).getDescription());
                break;
            default:
                break;
        }
    }

    public void loadResult(ObservableList<String> resArray) {
        resultList.setItems(resArray);
    }

}