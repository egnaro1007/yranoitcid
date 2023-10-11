package com.yranoitcid.Backend.Api;

import com.yranoitcid.Backend.Dictionary.word;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class googleChan extends api {

    private String text = "Hello, World!";

    public googleChan() {
        super("https://translate.googleapis.com/translate_a/single");
        addUserAgents(new String[]{
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36",
                // 13.5%
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36",
                // 6.6%
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0",
                // 6.4%
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0",
                // 6.2%
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36",
                // 5.2%
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
                // 4.8%
        });

        addPragma("client", "gtx");
        addPragma("sl", "auto");
        addPragma("tl", "vi");
        addPragma("dt", "t");
        addPragma("ie", "UTF-8");
        addPragma("oe", "UTF-8");
        addPragma("otf", "1");
        addPragma("ssel", "0");
        addPragma("tsel", "0");
        addPragma("kc", "7");
        addPragma("dt", "at");
        addPragma("dt", "bd");
        addPragma("dt", "ex");
        addPragma("dt", "ld");
        addPragma("dt", "md");
        addPragma("dt", "qca");
        addPragma("dt", "rw");
        addPragma("dt", "rm");
        addPragma("dt", "ss");
        addPragma("q", this.text);
    }

    public void search(String inputTerm) {
        this.text = inputTerm;
        this.editPragma("q", this.text);
        this.connect();
    }

    /**
     * Change the source language of the translation.
     *
     * @param language The language code ISO 639-1.
     */
    private void setSourceLanguage(String language) {
        this.editPragma("sl", language);
    }

    /**
     * Change the destination language of the translation.
     *
     * @param language The language code ISO 639-1.
     */
    private void setDestinationLanguage(String language) {
        this.editPragma("tl", language);
    }

    /**
     * Change the source and destination language of the translation.
     *
     * @param srcLang  The language code of source language by ISO 639-1.
     * @param destLang The language code of destination language by ISO 639-1.
     */
    public void setLanguage(String srcLang, String destLang) {
        this.setSourceLanguage(srcLang);
        this.setDestinationLanguage(destLang);
    }

    public void setLanguage(Character operator, String language) {
        if (operator == 's') {
            this.setSourceLanguage(language);
        } else if (operator == 'd') {
            this.setDestinationLanguage(language);
        } else {
            throw new RuntimeException("Invalid operator");
        }
    }

    @Override
    public Object parse() throws ParseException {
        // Parse the JSON string using org.json.simple.parser.JSONParser
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(this.getResponseAsString());

        // Prepare the result
        String word;
        word = text;
        String html;
        String description = "";
        String pronounce = "";

        // Parse
        if (obj instanceof JSONArray jsonArray
                && !jsonArray.isEmpty()
                && jsonArray.get(0) instanceof JSONArray innerArray1_0
                && !innerArray1_0.isEmpty()) {
            // System.out.println(jsonArray);
            if (innerArray1_0.get(0) instanceof JSONArray innerArray2_0_0 &&
                    !innerArray2_0_0.isEmpty()) {
                String value = (String) innerArray2_0_0.get(0);
                // System.out.println(value);
                description = value;
            }
            try {
                if (innerArray1_0.get(1) instanceof JSONArray innerArray2_0_1 &&
                        !innerArray2_0_1.isEmpty()) {
                    String value = (String) innerArray2_0_1.get(3);
                    System.out.println(value);
                    pronounce = value;
                }
            } catch (Exception e) {
                pronounce = "";
            }
            html = "<h1>" + word + "</h1>"
                    + "<h3><i>" + pronounce + "</i></h3>"
                    + "<ul><li>" + description + "</li></ul>";

            com.yranoitcid.Backend.Dictionary.word w = new word(word, html, description, pronounce);
            return w;
        }
        return null;
    }
}
