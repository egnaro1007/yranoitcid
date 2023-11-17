package com.yranoitcid.frontend;

import com.yranoitcid.backend.minigame.MultipleChoiceQuestion;
import com.yranoitcid.backend.minigame.MultipleChoices;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class MultipleChoiceController implements Initializable {
  @FXML
  private Label currentScore;
  private Integer currentScoreInt = 0;
  @FXML
  private Label highestScore;
  private Integer highestScoreInt = 0;
  @FXML
  private Label questionBox;
  @FXML
  private Button answer1;
  @FXML
  private Button answer2;
  @FXML
  private Button answer3;
  @FXML
  private Button answer4;

  private MultipleChoices game = new MultipleChoices();
  private List<Integer> unansweredQuestionId = new ArrayList<>();
  private int questionPointer = 0;
  MultipleChoiceQuestion currentQuestion = null;

  /**
   * Initialize the game.
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    game.loadQuestion();
    for (int i = 1; i <= game.getQuestions().size(); i++) {
      unansweredQuestionId.add(i);
    }
    reloadArray();
    System.out.println("MC Questionaries initialized successfully!");
  }

  /**
   * Start the game, generate 1 question.
   * If the game is already running, restart.
   */
  public void startGame() {
    if (!game.isRunning()) {
      System.out.println("Game starting...");
      game.setRunning(true);
      loadQuestion();
    } else {
      Alert startNewGame = new Alert(AlertType.CONFIRMATION);
      startNewGame.setTitle("NEW GAME");
      startNewGame.setHeaderText("Do you want to start a new game?");
      if (startNewGame.showAndWait().get() == ButtonType.OK) {
        game.setRunning(false);
        System.out.println("Restarting...");
        reloadArray();
        startGame();
      }
    }
  }
  private void loadQuestion() {
    int currentQuestionId = unansweredQuestionId.get(questionPointer);
    currentQuestion = game.getQuestions().get(currentQuestionId);
    questionPointer++;
    questionBox.setText(currentQuestion.getQuestion());
    String[] answerList = currentQuestion.getChoices();
    answer1.setText(answerList[0]);
    answer2.setText(answerList[1]);
    answer3.setText(answerList[2]);
    answer4.setText(answerList[3]);
  }

  /**
   * Choose answer 1.
   */
  public void answer1Check() {
    if (currentQuestion != null) {
      checkAnswer(game.answer(currentQuestion.getId(), 1));
    }
  }

  /**
   * Choose answer 2.
   */
  public void answer2Check() {
    if (currentQuestion != null) {
      checkAnswer(game.answer(currentQuestion.getId(), 2));
    }
  }

  /**
   * Choose answer 3.
   */
  public void answer3Check() {
    if (currentQuestion != null) {
      checkAnswer(game.answer(currentQuestion.getId(), 3));
    }
  }

  /**
   * Choose answer 4.
   */
  public void answer4Check() {
    if (currentQuestion != null) {
      checkAnswer(game.answer(currentQuestion.getId(), 4));
    }
  }
  private void checkAnswer(boolean isCorrect) {
    if (isCorrect) {
      System.out.println("You're correct!");
      // Insert extra effects here.
      scoreUpdate();
      loadQuestion();
    } else {
      System.out.println("You lost!");
      Alert lost = new Alert(AlertType.INFORMATION);
      lost.setTitle("YOU LOST!");
      lost.setHeaderText("Your score is " + game.getScore());
      lost.showAndWait();
      currentQuestion = null;
      reloadArray();
      game.setScore(0);
      scoreUpdate();
      game.setRunning(false);
    }
  }

  private void win() {
    reloadArray();
    game.setRunning(false);
    Alert win = new Alert(AlertType.INFORMATION);
    win.setTitle("YOU WON");
    win.setHeaderText("You won hahahaha!");
    win.showAndWait();
  }

  private void scoreUpdate() {
    currentScoreInt = game.getScore();
    currentScore.setText("Current Score: " + currentScoreInt);
    if (currentScoreInt > highestScoreInt) {
      highestScoreInt = currentScoreInt;
      highestScore.setText("Highest Score: " + highestScoreInt);
    }
  }

  private void reloadArray() {
    Collections.shuffle(unansweredQuestionId);
    questionPointer = 0;
  }

}
