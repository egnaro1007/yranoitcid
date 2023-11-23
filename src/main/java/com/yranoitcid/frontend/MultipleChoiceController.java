package com.yranoitcid.frontend;

import com.yranoitcid.backend.minigame.MultipleChoiceQuestion;
import com.yranoitcid.backend.minigame.MultipleChoices;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MultipleChoiceController implements Initializable {
  @FXML
  private Label currentScore;
  private Integer currentScoreInt = 0;
  @FXML
  private Label highestScore;
  private Integer highestScoreInt = 0;
  @FXML
  private HBox questionsButtonList;
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
  private List<Button> answerList = new ArrayList<>();

  private final MultipleChoices game = MultipleChoices.getInstance();
  private final List<Integer> questionId = new ArrayList<>();
  private int questionIndex = 0;
  private int questionIdPointer = 0;
  private int correctAnswerCount = 0;

  private List<QuizQuestion> currentQuestionList = new ArrayList<>(10);
  QuizQuestion currentQuestion = null;

  /**
   * Multiple-choice question, improvised to work in this game format.
   */
  private class QuizQuestion {
    public static final int UNANSWERED = 0;
    public static final int CORRECT = 1;
    public static final int INCORRECT = 2;
    private int index;
    private MultipleChoiceQuestion question;
    private int correctAnswer;
    private int state;
    private int answer;

    public QuizQuestion(int ID, int index) {
      this.question = game.getQuestions().get(ID);
      this.index = index;
      correctAnswer = question.getAnswer();
      state = UNANSWERED;
      answer = 0;
    }

    public int getState() {
      return this.state;
    }

    /**
     * Load question into the screen.
     * If answered, load the correct answer and chosen wrong answer (if exist).
     */
    public void loadQuestion() {
      questionBox.setText(question.getQuestion());
      String[] answers = question.getChoices();
      int i = 0;
      for (Button a: answerList) {
        a.getStyleClass().clear();
        a.getStyleClass().add("answer-btn");
        a.setText(answers[i]);
        i++;
      }
      switch (state) {
        case CORRECT:
          highlightCorrectAnswer();
          break;
        case INCORRECT:
          highlightCorrectAnswer();
          highlightWrongAnswer(answer);
          break;
        default:
          break;
      }
    }

    /**
     * Check whether the answer is correct.
     * If it is, upscore and highlight correct answer.
     * Else, kill the game and reset score to 0.
     * @param answer Answer 1 to 4.
     */
    public void checkAnswer(int answer) {
      if (game.answer(question.getId(), answer)) {
        state = CORRECT;
        correctAnswerCount++;
        scoreUpdate();
        highlightCorrectAnswer();
        markQuestionAsCorrect();

        if (correctAnswerCount == currentQuestionList.size()) {
          loadQuestionSetToGame();
          correctAnswerCount = 0;
          return;
        }

        currentQuestion = currentQuestionList.get(index + 1);
        currentQuestion.loadQuestion();
      } else {
        state = INCORRECT;
        this.answer = answer;
        highlightCorrectAnswer();
        highlightWrongAnswer(answer);
        markQuestionAsWrong();

        // "YOU LOST" popup.
        lose();
      }
    }

    private void markQuestionAsCorrect() {
      questionsButtonList.getChildren().get(index).getStyleClass().add("question-btn-correct");
    }

    private void markQuestionAsWrong() {
      questionsButtonList.getChildren().get(index).getStyleClass().add("question-btn-wrong");
    }

    private void highlightCorrectAnswer() {
      answerList.get(correctAnswer - 1).getStyleClass().set(0, "answer-btn-correct");
    }

    private void highlightWrongAnswer(int answer) {
      answerList.get(answer - 1).getStyleClass().set(0, "answer-btn-wrong");
    }
  }

  /**
   * Initialize the game.
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Make an indexed answer button list.
    answerList.add(answer1);
    answerList.add(answer2);
    answerList.add(answer3);
    answerList.add(answer4);

    // Load questions from the database to the game.
    game.loadQuestion();
    for (int i = 1; i <= game.getQuestions().size(); i++) {
      questionId.add(i);
    }
    questionsButtonList.setSpacing(10);
    reloadArray();
    System.out.println("MC Questionaries initialized successfully!");
  }

  /**
   * Start the game.
   * If the game is already running, restart.
   */
  public void startGame() {
    if (!game.isRunning()) {
      System.out.println("Game starting...");
      game.setRunning(true);
      game.setScore(0);
      reloadArray();
      loadQuestionSetToGame();
    } else {
      Alert startNewGame = new Alert(AlertType.CONFIRMATION);
      startNewGame.setTitle("NEW GAME");
      startNewGame.setHeaderText("Do you want to start a new game?");
      if (startNewGame.showAndWait().get() == ButtonType.OK) {
        game.setRunning(false);
        game.setScore(0);
        scoreUpdate();
        System.out.println("Restarting...");
        reloadArray();
        startGame();
      }
    }
  }

  private void loadQuestionSetToGame() {
    loadQuestionList();
    addQuestionButtons();

    // Load 1st question.
    currentQuestion = currentQuestionList.get(0);
    currentQuestion.loadQuestion();
  }

  /**
   * Reload global questions list, return ID pointer to 0.
   */
  private void reloadArray() {
    Collections.shuffle(questionId);
    questionIdPointer = 0;
    correctAnswerCount = 0;
    questionIndex = 0;
  }

  /**
   * Load 10 or fewer questions into the current questions list.
   * If called when all questions are answered correctly, win!
   */
  private void loadQuestionList() {
    if (questionIdPointer == questionId.size()) {
      win();
      return;
    }
    currentQuestionList.clear();
    int i = 0;
    int questionsArraySize = questionId.size();
    while (i < 10 && questionIdPointer != questionsArraySize) {
      currentQuestionList.add(new QuizQuestion(questionId.get(questionIdPointer), i));
      questionIdPointer++;
      i++;
    }
  }

  /**
   * Display question buttons.
   */
  private void addQuestionButtons() {
    if (game.isRunning()) {
      questionsButtonList.getChildren().clear();
      int i = 0;

      // New buttons to put into question button list.
      while (i < currentQuestionList.size()) {
        Button newQuestionButton = new Button();
        newQuestionButton.setText("Q" + (questionIndex++ + 1));
        newQuestionButton.getStyleClass().add("question-btn");
        int id = i;
        newQuestionButton.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            currentQuestion = currentQuestionList.get(id);
            currentQuestion.loadQuestion();
          }
        });
        questionsButtonList.getChildren().add(newQuestionButton);
        i++;
      }
      System.out.println("Button added");
    }
  }


  /**
   * Choose answer 1.
   */
  public void answer1Check() {
    if (currentQuestion != null
        && game.isRunning()
        && currentQuestion.getState() == QuizQuestion.UNANSWERED) {
      currentQuestion.checkAnswer(1);
    }
  }

  /**
   * Choose answer 2.
   */
  public void answer2Check() {
    if (currentQuestion != null
        && game.isRunning()
        && currentQuestion.getState() == QuizQuestion.UNANSWERED) {
      currentQuestion.checkAnswer(2);
    }
  }

  /**
   * Choose answer 3.
   */
  public void answer3Check() {
    if (currentQuestion != null
        && game.isRunning()
        && currentQuestion.getState() == QuizQuestion.UNANSWERED) {
      currentQuestion.checkAnswer(3);
    }
  }

  /**
   * Choose answer 4.
   */
  public void answer4Check() {
    if (currentQuestion != null
        && game.isRunning()
        && currentQuestion.getState() == QuizQuestion.UNANSWERED) {
      currentQuestion.checkAnswer(4);
    }
  }

  private void win() {
    game.setRunning(false);
    Alert win = new Alert(AlertType.INFORMATION);
    win.setTitle("YOU WON");
    win.setHeaderText("You won hahahaha!");
    win.showAndWait();
  }

  private void lose() {
    System.out.println("You lost!");
    Alert lost = new Alert(AlertType.INFORMATION);
    lost.setTitle("YOU LOST!");
    lost.setHeaderText("Your score is " + game.getScore());
    lost.showAndWait();
    game.setScore(0);
    scoreUpdate();
    game.setRunning(false);
  }

  /**
   * Update displaying score labels.
   */
  private void scoreUpdate() {
    currentScoreInt = game.getScore();
    currentScore.setText("Current Score: " + currentScoreInt);
    if (currentScoreInt > highestScoreInt) {
      highestScoreInt = currentScoreInt;
      highestScore.setText("Highest Score: " + highestScoreInt);
    }
  }
}
