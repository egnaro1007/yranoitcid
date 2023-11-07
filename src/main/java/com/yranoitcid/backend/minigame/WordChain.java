package com.yranoitcid.backend.minigame;

import com.yranoitcid.backend.dictionary.GameDictionary;
import com.yranoitcid.backend.dictionary.Word;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WordChain extends AbstractGame {

    GameDictionary dict = new GameDictionary("dict.db");
    private boolean running = false;
    private int state = 0;
    private final String srcLang;
    private final String destLang;
    private final Integer maxWordLength;
    LinkedHashMap<String, Word> usedWords = new LinkedHashMap<>();
    public WordChain(String srcLang, String destLang) {
        this(srcLang, destLang, Integer.MAX_VALUE);
    }

    public WordChain(String srcLang, String destLang, Integer maxWordLength) {
        super();
        this.srcLang = srcLang;
        this.destLang = destLang;
        this.maxWordLength = maxWordLength;
        if (srcLang.equals("en") && destLang.equals("vi")) {
            dict.initTable("en", "vi", "av");
        }

//        // Test
//        try {
//            ArrayList<Word> list = dict.getWordStartWith(srcLang, destLang, "a");
//            System.out.println(list.get(0).toString());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public void commit(String commitWord) {
        // Lowercase
        commitWord = commitWord.toLowerCase();

        // First round
        if (!isRunning()) {
            state = 0;
            this.setScore(0);
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
            return;
        }

        if (usedWords.containsKey(commitWord)) {
            this.lose();
            System.out.println("\u001B[31m" + "Word is used" + "\u001B[0m");
            return;
        }

        Word commitWordObj = dict.validateWord(srcLang, destLang, commitWord);
        if (commitWordObj == null) {
            this.lose();
            System.out.println("\u001B[31m" + "Word is invalid" + "\u001B[0m");
            return;
        }

        this.scoreUp();
        usedWords.put(commitWordObj.getWord(), commitWordObj);
        System.out.println("\u001B[32m" + "Your guess: " + commitWordObj.toString() + "\u001B[0m");
    }

    public String computerTurn() {
        if (usedWords.isEmpty()) {
            Word startWord = dict.getRandomWord(srcLang, destLang);
            usedWords.put(startWord.getWord(), startWord);
            return startWord.toString();
        }

        String lastWord = usedWords.lastEntry().getKey();
        // String startWith = lastWord.substring(lastWord.length() - 1);
        try {
            ArrayList<Word> list = dict.getWordStartWith(srcLang, destLang,
                    lastWord.substring(lastWord.length() - 1, lastWord.length()));
            Word newWord = list.get((int) Math.round(Math.random() * (list.size() - 1)));
            if (newWord != null) {
                usedWords.put(newWord.getWord(), newWord);
                System.out.println("\u001B[32m" + "Machine guess: " + newWord.toString());
                return newWord.getWord();
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
        state = 1;
        this.setRunning(false);
    }

    public void lose() {
        System.out.println("\u001B[31m" + "\u001B[1m" + "You lose"+ "\u001B[0m");
        state = 2;
        this.setRunning(false);
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public int getState() { return state; }

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
