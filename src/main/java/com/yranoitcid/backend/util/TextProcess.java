package com.yranoitcid.backend.util;

import java.util.ArrayList;

public class TextProcess {
    /**
     * Split a paragraph into sentences.
     *
     * <p>
     * A sentence is defined as a string of characters ending with a period, question mark, or
     * exclamation mark.
     *
     * @param paragraph A string is the input paragraph.
     * @return An ArrayList of sentences with their delimiters.
     */
    public static ArrayList<String> splitParagraphIntoSentences(String paragraph) {
        String[] sentenceArray = paragraph.splitWithDelimiters("[.?!]+", 0);

        ArrayList<String> sentences = new ArrayList<>();

        int numberOfSentences = sentenceArray.length;
        for (int i = 0; i < numberOfSentences; i += 2) {
            StringBuilder newSentence = new StringBuilder();
            newSentence.append(sentenceArray[i]);
            if (i + 1 < numberOfSentences) {
                newSentence.append(sentenceArray[i + 1].trim());
            } else {
                newSentence.append(".");
            }
            sentences.add(newSentence.toString());
        }

        return sentences;
    }
}
