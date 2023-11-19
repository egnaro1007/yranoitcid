package com.yranoitcid.backend.minigame;

import com.yranoitcid.backend.database.DatabaseQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class MultipleChoices extends AbstractGame {

    private boolean isRunning = false;
    HashMap<Integer, MultipleChoiceQuestion> questions = new HashMap<>();
    private DatabaseQuery database = new DatabaseQuery("dict.db");

    public void loadQuestion() {
        questions.clear();
        try (ResultSet rs = database.query("multiple_choice_quiz")) {
            while (rs.next()) {
                String[] choice = new String[4];
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

    public List<String> getQuestionsSet() {
        this.loadQuestion();

        List<MultipleChoiceQuestion> questions = new ArrayList<>(this.questions.values());
        questions.sort(new Comparator<MultipleChoiceQuestion>() {
            @Override
            public int compare(MultipleChoiceQuestion q1, MultipleChoiceQuestion q2) {
                return q1.getId() - q2.getId();
            }
        });

        List<String> questionsSet =  new ArrayList<>();
        for (MultipleChoiceQuestion question : questions) {
            questionsSet.add(question.getQuestion());
        }
        return questionsSet;
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
     *
     * @param questionID
     * @param answer
     * @return whether the answer is correct or not.
     */
    public boolean answer(int questionID, int answer) {
        try {
            if (checkAnswer(questionID, answer)) {
                super.scoreUp();
                return true;
            } else {
                return false;
            }
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the database with the new questions.
     * <p>
     * <b>Be careful!</b> This method will clear the database and replace it with the new.
     * <p>
     * The input stream must be a JSON file which has the following format:
     * <pre>
     * {
     *   "update": "1.0.0",
     *   "time": "00:57 09/11/2023",
     *   "questions": [
     *     {
     *       "question": "Michael rarely returns to his hometown, _______?",
     *       "description": "This is the first question.",
     *       "answer1": "doesn’t he",
     *       "answer2": "hasn’t he",
     *       "answer3": "does he",
     *       "answer4": "has he",
     *       "true_answer": 3
     *     },
     *     {
     *       "question": "I remember she wore a______dress to go out with her boyfriend last week.",
     *       "description": "This is the second question.",
     *       "answer1": "cotton white Vietnamese",
     *       "answer2": "Vietnamese white cotton",
     *       "answer3": "white Vietnamese cotton",
     *       "answer4": "white cotton Vietnamese",
     *       "true_answer": 3
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param inputStream The input stream from file.
     */
    public void updateDatabase(InputStream inputStream) {
        try {
            BufferedReader file = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonStr = new StringBuilder();
            while (file.ready()) {
                jsonStr.append(file.readLine().trim());
            }

            JSONObject jsonObj = new JSONObject(jsonStr.toString());
            System.out.println(jsonObj.getString("update"));
            System.out.println(jsonObj.getString("time"));

            JSONArray questionsArray = jsonObj.getJSONArray("questions");

            database.deleteAll("multiple_choice_quiz");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject obj = questionsArray.getJSONObject(i);
                String question = obj.getString("question");
                String description = obj.has("description") ? obj.getString("description") : null;
                String answer1 = obj.getString("answer1");
                String answer2 = obj.getString("answer2");
                String answer3 = obj.has("answer3") ? obj.getString("answer3") : null;
                String answer4 = obj.has("answer4") ? obj.getString("answer4") : null;
                Integer true_answer = obj.getInt("true_answer");

                HashMap<String, String> newQuestion = new HashMap<>();
                newQuestion.put("question", question);
                if (description != null) {
                    newQuestion.put("description", description);
                }
                newQuestion.put("answer1", answer1);
                newQuestion.put("answer2", answer2);
                if (answer3 != null) {
                    newQuestion.put("answer3", answer3);
                }
                if (answer4 != null) {
                    newQuestion.put("answer4", answer4);
                }
                newQuestion.put("true_answer", Integer.toString(true_answer));

                System.out.println(newQuestion);

                database.insert("multiple_choice_quiz", newQuestion);
            }


        } catch (IOException e) {
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
