package com.yranoitcid.backend.dictionary;

public class Word {

    private String word;
    private String html;
    private String description;
    private String pronounce;

    public Word(String word, String html) {
        this(word, html, "", "");
    }

    public Word(String word, String html, String description, String pronounce) {
        this.word = word;
        this.html = html;
        this.description = description;
        this.pronounce = pronounce;
    }

    public String getWord() {
        return word;
    }

    public String getHtml() {
        return html;
    }

    public String getDescription() {
        return description;
    }

    public String getPronounce() {
        return pronounce;
    }

    @Override
    public String toString() {
        return String.format("%-20s\t%-20s%s",
                word,
                ((pronounce == null) || pronounce.isEmpty()) ? "" : "\\" + pronounce + "\\",
                description);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean equals(Word word) {
        return this.word.equals(word.getWord());
    }

    public boolean equals(String word) {
        return this.word.equals(word);
    }
}
