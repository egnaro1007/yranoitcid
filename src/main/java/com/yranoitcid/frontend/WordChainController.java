package com.yranoitcid.frontend;

import com.yranoitcid.backend.minigame.WordChain;
import com.yranoitcid.backend.util.ShowAlert;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class WordChainController implements ShowAlert {
  private WordChain game = new WordChain("en" ,"vi", 10);
  @FXML
  private TextField playerInput;
  @FXML
  private Label machineInput;
  @FXML
  private ListView<String> playerInputList;
  @FXML
  private ListView<String> machineInputList;
  @FXML
  private Label currentScore;
  private Integer currentScoreInt = 0;
  @FXML
  private Label highestScore;
  private Integer highestScoreInt = 0;

  /**
   * Activate whenever player confirms their answer via pressing ENTER.
   */
  public void playerAnswer() {
    String answer = playerInput.getText();
    playerInput.clear();
    game.commit(answer);
    if (game.isRunning()) {
      playerInputList.getItems().add(answer);
      scoreUpdate();
      String machineAnswer = game.computerTurn();
      if (machineAnswer != null) {
        machineInput.setText(machineAnswer);
        machineInputList.getItems().add(machineAnswer);
      }
    }
    switch (game.getState()) {
      case 1:
        ShowAlert.showInformationAlert("YOU WON!",
                "You won against machine! Congratulation!",
                "Your score is: " + currentScoreInt);
        currentScoreInt = 0;
        clearLists();
        scoreUpdate();
        break;
      case 2:
        ShowAlert.showInformationAlert("YOU LOST...",
                "You lost against machine! Try harder next time!",
                "Your score is: " + currentScoreInt);
        currentScoreInt = 0;
        clearLists();
        scoreUpdate();
        break;
      default:
    }
  }

  /**
   * Update score number corresponding to current game's score.
   */
  private void scoreUpdate() {
    currentScoreInt = game.getScore();
    currentScore.setText("Current Score: " + currentScoreInt);
    if (currentScoreInt > highestScoreInt) {
      highestScoreInt = currentScoreInt;
      highestScore.setText("Highest Score: " + highestScoreInt);
    }
  }

  /**
   * Clear the list after a game is finished.
   */
  private void clearLists() {
    playerInputList.setItems(FXCollections.observableArrayList());
    machineInputList.setItems(FXCollections.observableArrayList());
  }
}
