package com.yranoitcid.frontend;

import com.yranoitcid.backend.dictionary.Word;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
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

import com.yranoitcid.backend.dictionary.Dictionary;
import com.yranoitcid.backend.util.BooleanExclusive;
import com.yranoitcid.backend.util.ANSIColor;

public class MothershipController implements Initializable{

    enum Menus {
        DICTIONARY,
        TRANSLATOR,
        EDITOR,
        WORDCHAIN,
        QUIZ
    }

    private final List<Parent> menus = new ArrayList<>();
    private final List<String> resourceLink = new ArrayList<>();
    private List<FadeTransition> selectedEffect = new ArrayList<>();
    private int mainPaneIndex = 0;
    private BooleanExclusive isMenuOpen = null;

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
    private StackPane toDictionarySelected;
    @FXML
    private StackPane toTranslatorContainer;
    @FXML
    private StackPane toTranslatorMask;
    @FXML
    private StackPane toTranslatorSelected;
    @FXML
    private StackPane toEditorContainer;
    @FXML
    private StackPane toEditorMask;
    @FXML
    private StackPane toEditorSelected;
    @FXML
    private StackPane toWordChainContainer;
    @FXML
    private StackPane toWordChainMask;
    @FXML
    private StackPane toWordChainSelected;
    @FXML
    private StackPane toQuizContainer;
    @FXML
    private StackPane toQuizMask;
    @FXML
    private StackPane toQuizSelected;
    @FXML
    private StackPane reloadCSSContainer;
    @FXML
    private StackPane reloadCSSMask;

    @FXML
    private Label randomWord;
    @FXML
    private Label randomWordDescription;

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
        resourceLink.add("/fxml/multiplechoice.fxml");

        for (int i = 0; i < resourceLink.size(); i++) {
            FXMLLoader load = new FXMLLoader(Application.class.getResource(
                resourceLink.get(i)));
            try {
                menus.add(load.load());
//                System.out.println(load.getLocation() + " loaded successfully.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HBox.setHgrow(menus.get(i), Priority.ALWAYS);
        }
        isMenuOpen = new BooleanExclusive(resourceLink.size());

        System.out.println("Opening menu initialized successfully.");


        // Set the index of the main pane
        mainPaneIndex = mothership.getChildren().size() - 1;

        // Live clock
        Timeline clock = getTimeline();
        clock.play();

        // Random word
        Dictionary dictionary = Dictionary.getInstance("dict.db");
        dictionary.initTable("en", "vi", "av");
        Word random;
        do {
            random = dictionary.getRandomWord("en", "vi");
        } while (random.getWord().isEmpty() || random.getDescription().isEmpty());
        randomWord.setText(random.getWord());
        randomWordDescription.setText(random.getDescription());


        // Add animation to the buttons
        addWipeEffect(toDictionaryContainer, toDictionaryMask);
        addSelectedFadeEffect(toDictionarySelected, selectedEffect);
        addWipeEffect(toTranslatorContainer, toTranslatorMask);
        addSelectedFadeEffect(toTranslatorSelected, selectedEffect);
        addWipeEffect(toEditorContainer, toEditorMask);
        addSelectedFadeEffect(toEditorSelected, selectedEffect);
        addWipeEffect(toWordChainContainer, toWordChainMask);
        addSelectedFadeEffect(toWordChainSelected, selectedEffect);
        addWipeEffect(toQuizContainer, toQuizMask);
        addSelectedFadeEffect(toQuizSelected, selectedEffect);
        addWipeEffect(reloadCSSContainer, reloadCSSMask);
    }

    public void switchToDictionary() {
        switchMainPane(Menus.DICTIONARY.ordinal());
//        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.DICTIONARY.ordinal()));
    }

    public void switchToTranslator() {
        switchMainPane(Menus.TRANSLATOR.ordinal());
//        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.TRANSLATOR.ordinal()));
    }

    public void switchToEditor() {
        switchMainPane(Menus.EDITOR.ordinal());
//        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.EDITOR.ordinal()));
    }

    public void switchToWordChain() {
        switchMainPane(Menus.WORDCHAIN.ordinal());
//        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.WORDCHAIN.ordinal()));
    }

    public void switchToQuiz() {
        switchMainPane(Menus.QUIZ.ordinal());
//        mothership.getChildren().set(mainPaneIndex, menus.get(Menus.QUIZ.ordinal()));
    }

    private void switchMainPane(int index) {
        int oldIndex = isMenuOpen.getTrue();
        isMenuOpen.setTrue(index);
        int newIndex = isMenuOpen.getTrue();

        if (oldIndex != BooleanExclusive.ALL_FALSE_INDEX && oldIndex != newIndex){
            selectedEffect.get(oldIndex).stop();
            selectedEffect.get(oldIndex).setFromValue(1);
            selectedEffect.get(oldIndex).setToValue(0);
            selectedEffect.get(oldIndex).setRate(-1);
            selectedEffect.get(oldIndex).playFromStart();
        }

        selectedEffect.get(newIndex).setFromValue(0.3);
        selectedEffect.get(newIndex).setToValue(1);
        selectedEffect.get(newIndex).playFromStart();

        if (newIndex != oldIndex) {
            mothership.getChildren().set(mainPaneIndex, menus.get(newIndex));

            ANSIColor.consoleColorPrint(
                    "Menu selected: " + index + " " + Menus.values()[index], ANSIColor.GREEN);
        } else {
            ANSIColor.consoleColorPrint("Already selected!", ANSIColor.GREEN);
        }
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

    private void addWipeEffect(Pane container, Pane mask) {
        addWipeEffect(container, mask, Duration.millis(300));
    }

    private void addWipeEffect(Pane container, Pane mask, Duration duration) {
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

            mask.setVisible(true);
            tt.playFromStart();
        });
        container.setOnMouseExited(e -> {
            tt.setRate(1);
            tt.setFromX(mask.getTranslateX());
            tt.setToX(container.getLayoutX()+container.getWidth());

            tt.playFromStart();
            tt.setOnFinished(event -> {
                mask.setVisible(false);
                tt.setOnFinished(null);
            });
        });
    }

    private void addSelectedFadeEffect(Pane button, List<FadeTransition> selectedEffectList) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), button);
        ft.setFromValue(0.3);
        ft.setToValue(1);
        selectedEffectList.add(ft);
    }

    public void reloadCSS() {
//        Scene scene = mothership.getScene();
//        if (scene != null) {
//            System.out.println(scene.getStylesheets());
//            String css = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
//            scene.getStylesheets().clear();
//            scene.getStylesheets().add(css);
//            System.out.println("CSS reloaded successfully.");
//        } else {
//            Alert alert = new Alert(AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Cannot load scene");
//            alert.setContentText("Something went wrong. Cannot load Mothership");
//            alert.showAndWait();
//        }
        reloadStyleSheets(mothership);
    }

    public void reloadStyleSheets(Parent parent) {
        Scene scene = parent.getScene();
        if (scene != null) {
            System.out.println("Reload all stylesheets:");
            ObservableList<String> stylesheets = scene.getStylesheets();
            ArrayList<String> stylesheetsToCopy = new ArrayList<>();
            for (String stylesheet : stylesheets) {
                stylesheetsToCopy.add(stylesheet);
                System.out.println("\tStylesheet found: " + stylesheet);
            }

            System.out.println("\t-----------");

            scene.getStylesheets().clear();
            for (String stylesheet : stylesheetsToCopy) {
                scene.getStylesheets().add(stylesheet);
                System.out.println("\tReload successfully: " + stylesheet);
            }
            System.out.println("Stylesheets reloaded successfully.");
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot load scene");
            alert.setContentText("Something went wrong. Cannot load " + parent.getClass().getName());
            alert.showAndWait();
        }
    }
}
