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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class dictionaryController implements Initializable{

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
    ArrayList<word> putDataHere = new ArrayList<word>();
    ObservableList<String> resultListDisplay = FXCollections.observableArrayList();

    @FXML
    private TextField searchInput;
    String keyword;

    @FXML
    private ChoiceBox<String> searchModeChoiceBox;
    int searchModeChoice = 0;

    Stage stage;

    dictionary workingDictionary = new dictionary("dict.db");

    /** Initialize the window. This will include the dictionary and search option choice box.
     *  Called every time the app starts.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
            workingDictionary.initTable("en", "vi", "av");
        } catch(RuntimeException e) {
            System.out.println("Error in initiating the dictionary.");
        }
        ObservableList<String> searchModeOptions = FXCollections.observableArrayList(
            "Contains...",
            "Full match"
        );
        searchModeChoiceBox.setItems(searchModeOptions);
        searchModeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchModeChoice = searchModeChoiceBox.getSelectionModel().getSelectedIndex();
                System.out.println("Current mode: " + searchModeChoice);
            }
            
        });
        System.out.println("Dictionary window created successfully!");
    }
    
    /** Handles logout event.
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

    /** Get the keyword from text field immediately after input is detected 
     * and fetch results directly into the list.
     * @throws Exception
     */
    public void getKeyword() throws Exception {
        keyword = searchInput.getText();
        fetchResult(searchModeChoice);
        System.out.println(keyword);
    }

    public void fetchResult(int searchModeChoice) throws Exception {
        putDataHere.clear();
        resultListDisplay.clear();
        switch (searchModeChoice) {
            case 0:
                putDataHere = workingDictionary.searchContains("en", "vi", keyword);
                for (int i = 0; i < (putDataHere.size() < 30 ? putDataHere.size() : 30); i++) {
                    resultListDisplay.add(putDataHere.get(i).getWord());
                }
                loadResult(resultListDisplay);
                resultList.getSelectionModel().selectedItemProperty().addListener(
                    new WeakChangeListener<>(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                            int id = resultList.getSelectionModel().getSelectedIndex();
                            resultWord.setText(putDataHere.get(id).getWord());
                            resultPronun.setText(putDataHere.get(id).getPronounce());
                            resultDesc.setText(putDataHere.get(id).getDescription());
                        }
                }));
                break;
            case 1:
                putDataHere.clear();
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