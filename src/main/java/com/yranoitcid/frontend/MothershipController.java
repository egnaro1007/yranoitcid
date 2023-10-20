package com.yranoitcid.frontend;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MothershipController implements Initializable{
    private Parent dictionaryMenu;
    private Parent translatorMenu;
    private Parent editorMenu;

    @FXML
    private VBox mothership;

    /**
     *  Initialize the app.
     *  Called whenever the app starts.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader load = new FXMLLoader(Application.class.getResource(
                "/fxml/dictionary.fxml"));
        try {
            dictionaryMenu = load.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FXMLLoader load1 = new FXMLLoader(Application.class.getResource(
                "/fxml/translator.fxml"));
        try {
            translatorMenu = load1.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FXMLLoader load2 = new FXMLLoader(Application.class.getResource(
                "/fxml/editor.fxml"));
        try {
            editorMenu = load2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VBox.setVgrow(dictionaryMenu, Priority.ALWAYS);
        VBox.setVgrow(translatorMenu, Priority.ALWAYS);
        VBox.setVgrow(editorMenu, Priority.ALWAYS);

        System.out.println("Opening menu initialized successfully.");
    }

    public void switchToDictionary() {
        mothership.getChildren().set(0, dictionaryMenu);
    }

    public void switchToTranslator() {
        mothership.getChildren().set(0, translatorMenu);
    }

    public void switchToEditor() {
        mothership.getChildren().set(0, editorMenu);
    }

}
