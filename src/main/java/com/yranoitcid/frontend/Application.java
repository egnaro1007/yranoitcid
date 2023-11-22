package com.yranoitcid.frontend;

import java.io.File;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    ObservableList<String> randomTitles = FXCollections.observableArrayList(
        "Yranoitcid!",
        "Hello Dictionary!",
        "Just an ordinary Dictionary?",
        "Cool Dictionary!"
    );

    @Override
    public void start(Stage stage) throws IOException {

        // Load font
        loadFontFromDirectory("/font");

        // Load data.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/mothership.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm()
        );

        // Setting the stage for show.
        Image icon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/image/icon.png")));
        stage.getIcons().add(icon);

        stage.setTitle(randomTitles.get((int) (Math.random() * randomTitles.size())));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage);
        });
    }

    /**
     * Logout prompt.
     * @param stage
     */
    public void logout(Stage stage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Really");
        alert.setHeaderText("Did you just clicked the exit button??");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    /**
     * Root folder is resource folder.
     * @param folderPath Path to to font folder, start from "/resource".
     */
    private void loadFontFromDirectory(String folderPath) {
        loadFontFromDirectory(new File(
                Objects.requireNonNull(Application.class.getResource(folderPath)).getFile()));
    }

    private void loadFontFromDirectory(File folder) {
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                loadFontFromDirectory(file);
            } else if (file.getName().toLowerCase().endsWith("ttf")
                    || file.getName().toLowerCase().endsWith("otf")) {
//                System.out.println(file.toURI().toString());
                Font.loadFont(getClass().getResourceAsStream(file.toURI().toString()),
                        Font.getDefault().getSize());
            }
        }
    }


    public static void main(String[] args) {
        launch();
    }
}