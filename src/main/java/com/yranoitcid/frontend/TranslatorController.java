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
        languageSelectSrc.setItems(FXCollections.observableArrayList("English (en)", "Vietnamese (vi)"));
        languageSelectSrc.setValue("English (en)");

        languageSelectDes.setItems(FXCollections.observableArrayList("English (en)", "Vietnamese (vi)"));
        languageSelectDes.setValue("Vietnamese (vi)");

        System.out.println("Translator created successfully!");
    }

    public void translate() {
        String text = inputTranslate.getText();
        String langSrc = languageSelectSrc.getValue();
        String langDes = languageSelectDes.getValue();
        グーグルちゃん.setLanguage(langSrc.substring(langSrc.length() - 3, langSrc.length() - 1),
                                 langDes.substring(langDes.length() - 3, langDes.length() - 1));
        System.out.println(text);
        System.out.println("Result: " + グーグルちゃん.search(text));
        resultTranslate.setText(グーグルちゃん.search(text).getDescription());
    }
}
