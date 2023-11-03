package com.yranoitcid.frontend;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MothershipController implements Initializable{

    enum Menus {
        DICTIONARY,
        TRANSLATOR,
        EDITOR,
        WORDCHAIN
    }

    private final List<Parent> menus = new ArrayList<>();
    private final List<String> resourceLink = new ArrayList<>();
    private int mainPaneIndex = 0;

    @FXML
    private HBox mothership;

    @FXML
    private Label time;
    @FXML
    private Label date;

    @FXML
    private VBox leftSideBar;
    @FXML
    private VBox mainPane;


    @FXML
    private StackPane toDictionaryContainer;
    @FXML
    private StackPane toDictionaryMask;
    @FXML
    private StackPane toTranslatorContainer;
    @FXML
    private StackPane toTranslatorMask;
    @FXML
    private StackPane toEditorContainer;
    @FXML
    private StackPane toEditorMask;
    @FXML
    private StackPane toWordChainContainer;
    @FXML
    private StackPane toWordChainMask;
    @FXML
    private StackPane reloadCSSContainer;
    @FXML
    private StackPane reloadCSSMask;

    /**
     *  Initialize the app.
     *  Called whenever the app starts.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        resourceLink.add("/fxml/dictionary.fxml");
        resourceLink.add("/fxml/translator.fxml");
        resourceLink.add("/fxml/editor.fxml");
        resourceLink.add("/fxml/wordchain.fxml");

        for (int i = 0; i < 4; i++) {
            FXMLLoader load = new FXMLLoader(Application.class.getResource(
                resourceLink.get(i)));
            try {
                menus.add(load.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            VBox.setVgrow(menus.get(i), Priority.ALWAYS);
        }

        System.out.println("Opening menu initialized successfully.");

        // Set the index of the main pane
        mainPaneIndex = mothership.getChildren().size() - 1;

        // Live clock
        Timeline clock = getTimeline();
        clock.play();

        // Add animation to the buttons
        addTransition(toDictionaryContainer, toDictionaryMask);
        addTransition(toTranslatorContainer, toTranslatorMask);
        addTransition(toEditorContainer, toEditorMask);
        addTransition(toWordChainContainer, toWordChainMask);
        addTransition(reloadCSSContainer, reloadCSSMask);
    }


    private Timeline getTimeline() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO,e -> {
                String timeFormat = ((LocalDateTime.now().getNano() / 600_000_000) % 2 == 0) ? "HH:mm:ss" : "HH mm ss";
                time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat)));
                date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }),
                new KeyFrame(Duration.seconds(0.1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        return clock;
    }

    private void addTransition (Pane container, Pane mask) {
        addTransition(container, mask, Duration.millis(300));
    }

    private void addTransition (Pane container, Pane mask, Duration duration) {
        TranslateTransition tt = new TranslateTransition(duration, mask);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        container.setOnMouseEntered(e -> {
            tt.setRate(0.5);
            if (mask.getTranslateX() < container.getLayoutX()) {
                tt.setFromX(mask.getTranslateX());

            } else {
                tt.setFromX(container.getLayoutX()-container.getWidth());
            }
            tt.setToX(container.getLayoutX());
            tt.playFromStart();
        });
        container.setOnMouseExited(e -> {
            tt.setRate(1);

            tt.setFromX(mask.getTranslateX());
            tt.setToX(container.getLayoutX()+container.getWidth());
            tt.playFromStart();
        });
    }

    public void switchToDictionary() {
        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.DICTIONARY.ordinal()));
    }

    public void switchToTranslator() {
        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.TRANSLATOR.ordinal()));
    }

    public void switchToEditor() {
        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.EDITOR.ordinal()));
    }

    public void switchToWordChain() {
        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.WORDCHAIN.ordinal()));
    }

    public void reloadCSS() {
        Scene scene = mothership.getScene();
        if (scene != null) {
            String css = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            System.out.println("CSS reloaded successfully.");
        } else {
        // Show an error message if the button is not in a scene
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Button not in a scene");
        alert.setContentText("The button cannot reload CSS because it's not in a scene.");
        alert.showAndWait();
        }
    }
}
