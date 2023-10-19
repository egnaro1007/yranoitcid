package com.yranoitcid.frontend;

import com.yranoitcid.backend.api.GoogleChan;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class TranslatorController implements Initializable{
    
    @FXML
    private TextArea inputTranslate;
    @FXML
    private Label resultTranslate;
    @FXML
    private Button search;
    @FXML
    private ChoiceBox<String> languageSelectSrc;
    @FXML
    private ChoiceBox<String> languageSelectDes;

    GoogleChan グーグルちゃん = new GoogleChan();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        languageSelectSrc.setItems(FXCollections.observableArrayList("ENG", "VIE"));
        languageSelectSrc.setValue("ENG");

        languageSelectDes.setItems(FXCollections.observableArrayList("ENG", "VIE"));
        languageSelectDes.setValue("VIE");

        System.out.println("Translator created successfully!");
    }

    public void translate() {
        String text = inputTranslate.getText();
        System.out.println(text);
        System.out.println("Result: " + グーグルちゃん.search(text));
        resultTranslate.setText(グーグルちゃん.search(text).getDescription());
    }
}
