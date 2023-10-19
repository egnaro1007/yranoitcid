package com.yranoitcid.Backend.Dictionary;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class gameDictionary extends dictionary {

    public gameDictionary(String dbPath) {
        super(dbPath);
        super.setLimit(100);
    }

    public ArrayList<word> getWordStartWith(String srcLang, String destLang, String startWith)
            throws Exception {
        return getWordStartWith(srcLang, destLang, startWith, Integer.MAX_VALUE);
    }

    public ArrayList<word> getWordStartWith(String srcLang, String destLang, String startWith,
            Integer maxWordLength)
            throws Exception {
        ArrayList<word> list = searchContains(srcLang, destLang, startWith);

        list.removeIf(word ->
                    // Remove word that is too long
                    word.getWord().length() > maxWordLength
                    // Only keep word that start with startWith
                || !word.getWord().toLowerCase().startsWith(startWith.toLowerCase())
                    // Remove word that contain special character
                || !word.getWord().matches("[a-zA-Z]+")
        );

        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    public word validateWord(String srcLang, String destLang, String inputWord) {
        try {
            word queryWord = searchExact(srcLang, destLang, inputWord);
            return queryWord;
        } catch (Exception e) {
            return null;
        }
    }
}
