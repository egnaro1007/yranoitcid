package com.yranoitcid.backend.minigame;

public class MultipleChoiceQuestion {

    private Integer id;
    private String question;
    private String description;
    private String[] choices;
    private Integer answer;

    public MultipleChoiceQuestion(Integer id, String question, String description, String[] choices,
            Integer answer) {
        this.id = id;
        this.question = question;
        this.description = description;
        this.choices = choices;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return question;
    }

    public Integer getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return description;
    }

    public String[] getChoices() {
        return choices;
    }

    public String getChoice(Integer index) {
        return choices[index - 1];
    }

    public Integer getAnswer() {
        return answer;
    }
}
