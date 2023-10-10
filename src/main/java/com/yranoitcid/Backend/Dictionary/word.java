package com.yranoitcid.Backend.Dictionary;

public class word {

    private String word;
    private String html;
    private String description;
    private String pronounce;

    public word(String word, String html) {
        this(word, html, "", "");
    }

    public word(String word, String html, String description, String pronounce) {
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
}
