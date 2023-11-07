package com.yranoitcid.frontend;

import com.yranoitcid.backend.minigame.WordChain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class WordChainController {
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
        Alert win = new Alert(AlertType.INFORMATION);
        win.setTitle("YOU WON!");
        win.setHeaderText("You won against machine! Congratulation!");
        win.setContentText("Your score is: " + currentScoreInt);
        if (win.showAndWait().get() == ButtonType.OK) {
          System.out.println("You won Word Chain!");
        }
        currentScoreInt = 0;
        clearLists();
        scoreUpdate();
        break;
      case 2:
        Alert lose = new Alert(AlertType.WARNING);
        lose.setTitle("YOU LOST...");
        lose.setHeaderText("You lost against machine! Try harder next time!");
        lose.setContentText("Your score is: " + currentScoreInt);
        if (lose.showAndWait().get() == ButtonType.OK) {
          System.out.println("You lost Word Chain!");
        }
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
