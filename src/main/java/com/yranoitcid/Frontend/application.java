package com.yranoitcid.Frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class application extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //Loading data.
        FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("/com/yranoitcid/Frontend/mothership.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image icon = new Image("approv.png");

        //Setting the stage for show.
        stage.getIcons().add(icon);
        stage.setTitle("Hello dictionary!");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage);
        });
    }

    public void logout(Stage stage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Really");
        alert.setHeaderText("Did you just clicked the exit button??");
        alert.setContentText("Do you want to exist?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close(); 
        }
    }

    public static void main(String[] args) {
        launch();
    }
}