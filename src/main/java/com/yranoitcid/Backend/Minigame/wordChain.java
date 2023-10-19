package com.yranoitcid.Backend.Minigame;

import com.yranoitcid.Backend.Dictionary.gameDictionary;
import com.yranoitcid.Backend.Dictionary.word;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class wordChain extends abstractGame {

    gameDictionary dict = new gameDictionary("dict.db");
    private boolean running = false;
    private final String srcLang;
    private final String destLang;
    private final Integer maxWordLength;
    LinkedHashMap<String, word> usedWords = new LinkedHashMap<>();

    public wordChain(String srcLang, String destLang) {
        this(srcLang, destLang, Integer.MAX_VALUE);
    }

    public wordChain(String srcLang, String destLang, Integer maxWordLength) {
        super();
        this.srcLang = srcLang;
        this.destLang = destLang;
        this.maxWordLength = maxWordLength;
        if (srcLang.equals("en") && destLang.equals("vi")) {
            dict.initTable("en", "vi", "av");
        }

//        // Test
//        try {
//            ArrayList<word> list = dict.getWordStartWith(srcLang, destLang, "a");
//            System.out.println(list.get(0).toString());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public void commit(String commitWord) {
        // First round
        if (!isRunning()) {
            if (commitWord.isEmpty()) {
                System.out.println("Computer first");
                this.setRunning(true);
                System.out.println(computerTurn());
                return;
            }
            else {
                this.setRunning(true);
            }
        }

        if (commitWord.length() > maxWordLength) {
            System.out.println("\u001B[31m" + "Word is too long" + "\u001B[0m");
        }

        if (usedWords.containsKey(commitWord)) {
            this.lose();
            System.out.println("\u001B[31m" + "Word is used" + "\u001B[0m");
        }

        word commitWordObj = dict.validateWord(srcLang, destLang, commitWord);
        if (commitWordObj == null) {
            this.lose();
            System.out.println("\u001B[31m" + "Word is not valid" + "\u001B[0m");
        }

        this.scoreUp();
        assert commitWordObj != null;
        usedWords.put(commitWordObj.getWord(), commitWordObj);
        System.out.println("\u001B[32m" + "Your guest: " + commitWordObj.toString() + "\u001B[0m");
    }

    protected String computerTurn() {
        if (usedWords.isEmpty()) {
            word startWord = dict.getRandomWord(srcLang, destLang);
            usedWords.put(startWord.getWord(), startWord);
            return startWord.toString();
        }

        String lastWord = usedWords.lastEntry().getKey();
        // String startWith = lastWord.substring(lastWord.length() - 1);
        try {
            ArrayList<word> list = dict.getWordStartWith(srcLang, destLang,
                    lastWord.substring(lastWord.length() - 1, lastWord.length()));
            word newWord = list.get((int) Math.round(Math.random() * (list.size() - 1)));
            if (newWord != null) {
                usedWords.put(newWord.getWord(), newWord);
                return newWord.toString();
            } else {
                this.win();
                return null;
            }
        } catch (Exception e) {
            this.win();
        }
        return null;
    }

    public void win() {
        System.out.println("\u001B[32m" + "\u001B[1m" + "Congratulation! Your score is: "
                + getScore().toString() + "\u001B[0m");
        this.setRunning(false);
    }

    public void lose() {
        System.out.println("\u001B[31m" + "\u001B[1m" + "You lose"+ "\u001B[0m");
        this.setRunning(false);
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    //    public void startGame(String srcLang, String destLang) {
//        this.startGame(srcLang, destLang, Integer.MAX_VALUE);
//    }
//
//    @Override
//    public void startGame(String srcLang, String destLang, Integer maxWordLength) {
//        sectionStartTime = System.currentTimeMillis();
//        // return list.get((int) Math.round(Math.random() * (list.size() - 1)));
//    }
}
