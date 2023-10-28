package com.yranoitcid.frontend;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MothershipController implements Initializable{

    enum Menus {
        DICTIONARY,
        TRANSLATOR,
        EDITOR,
        WORDCHAIN
    }

    private List<Parent> menus = new ArrayList<>();
    private List<String> resourceLink = new ArrayList<>();

    @FXML
    private VBox mothership;

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
    }

    public void switchToDictionary() {
        mothership.getChildren().set(0, menus.get(Menus.DICTIONARY.ordinal()));
    }

    public void switchToTranslator() {
        mothership.getChildren().set(0, menus.get(Menus.TRANSLATOR.ordinal()));
    }

    public void switchToEditor() {
        mothership.getChildren().set(0, menus.get(Menus.EDITOR.ordinal()));
    }

    public void switchToWordChain() {
        mothership.getChildren().set(0, menus.get(Menus.WORDCHAIN.ordinal()));
    }

}
