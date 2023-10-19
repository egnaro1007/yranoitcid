package com.yranoitcid.Backend.Minigame;

public abstract class abstractGame {
    protected Long sectionStartTime = 0L;
    private Integer score = 0;
    private Long timeLimit;

    public abstractGame() {
        this.timeLimit = Long.MAX_VALUE;
    }

    public abstractGame(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getScore() {
        return score;
    }

    private void setScore(Integer score) {
        this.score = score;
    }

    public void scoreModify(Integer value) {
        Integer newScore = this.score + value;
        setScore(newScore);
    }

    public void scoreUp() {
        this.scoreModify(1);
    }

    public void scoreDown() {
        this.scoreModify(-1);
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }
}
