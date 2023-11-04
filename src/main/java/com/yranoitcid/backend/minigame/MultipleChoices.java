package com.yranoitcid.backend.minigame;

import com.yranoitcid.backend.database.DatabaseQuery;
import java.security.InvalidKeyException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MultipleChoices extends AbstractGame {
    private boolean isRunning = false;
    HashMap<Integer, MultipleChoiceQuestion> questions = new HashMap<>();

    public void loadQuestion() {
        DatabaseQuery database = new DatabaseQuery("dict.db");
        try (ResultSet rs = database.query("mutilchoiceQuiz")) {
            while (rs.next()) {
                String choice[] = new String[4];
                choice[0] = rs.getString("answer1");
                choice[1] = rs.getString("answer2");
                choice[2] = rs.getString("answer3");
                choice[3] = rs.getString("answer4");

                MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(
                        rs.getInt("id"),
                        rs.getString("question"),
                        rs.getString("description"),
                        choice,
                        rs.getInt("true_answer")
                );
                questions.put(newQuestion.getId(), newQuestion);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<Integer, MultipleChoiceQuestion> getQuestions() {
        if (questions.isEmpty()) {
            this.loadQuestion();
        }
        return questions;
    }

    private boolean checkAnswer(int questionID, int answer) throws InvalidKeyException {
        MultipleChoiceQuestion ques = questions.get(questionID);
        if (ques.getChoice(answer).isBlank()) {
            throw new InvalidKeyException("Invalid answer");
        }
        return ques.getAnswer().equals(answer);
    }

    /**
     * Check if the answer is correct.
     * @param questionID
     * @param answer
     * @return whether the answer is correct or not.
     */
    public boolean answer(int questionID, int answer) {
        try {
            if (checkAnswer(questionID,answer)) {
                super.scoreUp();
                return true;
            } else {
                return false;
            }
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
