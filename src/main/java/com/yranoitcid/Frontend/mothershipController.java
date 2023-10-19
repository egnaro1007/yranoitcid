package com.yranoitcid.Frontend;

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

public class mothershipController implements Initializable{
    private Parent dictionaryMenu;
    private Parent translatorMenu;
    
    @FXML
    private Button toDictionary;
    @FXML
    private Button toTranslator;

    @FXML
    private VBox mothership;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader load = new FXMLLoader(application.class.getResource("/com/yranoitcid/Frontend/dictionary.fxml"));
        try {
            dictionaryMenu = load.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FXMLLoader load1 = new FXMLLoader(application.class.getResource("/com/yranoitcid/Frontend/translator.fxml"));
        try {
            translatorMenu = load1.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VBox.setVgrow(dictionaryMenu, Priority.ALWAYS);
        VBox.setVgrow(translatorMenu, Priority.ALWAYS);
    }

    public void switchToDictionary() {
        mothership.getChildren().set(0, dictionaryMenu);
    }

    public void switchToTranslator() {
        mothership.getChildren().set(0, translatorMenu);
    }

}
