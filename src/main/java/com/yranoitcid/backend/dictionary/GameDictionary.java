package com.yranoitcid.backend.dictionary;

import java.util.ArrayList;

public class GameDictionary extends Dictionary {

    public GameDictionary(String dbPath) {
        super(dbPath);
        super.setLimit(100);
    }

    public ArrayList<Word> getWordStartWith(String srcLang, String destLang, String startWith)
            throws Exception {
        return getWordStartWith(srcLang, destLang, startWith, Integer.MAX_VALUE);
    }

    public ArrayList<Word> getWordStartWith(String srcLang, String destLang, String startWith,
            Integer maxWordLength)
            throws Exception {
        ArrayList<Word> list = searchContains(srcLang, destLang, startWith);

        list.removeIf(word ->
                    // Remove Word that is too long
                    word.getWord().length() > maxWordLength
                    // Only keep Word that start with startWith
                || !word.getWord().toLowerCase().startsWith(startWith.toLowerCase())
                    // Remove Word that contain special character
                || !word.getWord().matches("[a-zA-Z]+")
        );

        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    public Word validateWord(String srcLang, String destLang, String inputWord) {
        try {
            Word queryWord = searchExact(srcLang, destLang, inputWord);
            return queryWord;
        } catch (Exception e) {
            return null;
        }
    }
}
