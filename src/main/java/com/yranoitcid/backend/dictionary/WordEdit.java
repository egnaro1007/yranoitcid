package com.yranoitcid.backend.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordEdit extends Word {

    private String text;

    public WordEdit() {
        super();
    }

    public WordEdit(Word word) {
        super();
        loadWord(word);
    }

    public void loadWord(Word word) {
        super.word = word.getWord();
        super.html = word.getHtml();
        super.description = word.getDescription();
        super.pronounce = word.getPronounce();
        this.text = htmlToString(word.getHtml());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) throws IOException {
        this.text = text;
        super.html = stringToHtml(text);
        super.word = getWord(super.html);
        super.pronounce = getPronounce(super.html);
    }

    public void setHtml(String html) {
        super.html = html;
        this.text = htmlToString(html);
        super.word = getWord(super.html);
        super.pronounce = getPronounce(super.html);
    }

    public void setWord(String word) {
        Document doc = Jsoup.parse(super.html);
        Elements elements = doc.body().children();

        for (Element element : elements) {
            if (element.tagName().equals("h1")) {
                element.text(word);
                break;
            }
        }

        super.word = word;
        this.setHtml(doc.html());
    }

    public void setPronounce(String pronounce) {
        Document doc = Jsoup.parse(super.html);
        Elements elements = doc.body().children();

        for (Element element : elements) {
            if (element.tagName().equals("h3")) {
                element.text(pronounce);
                break;
            }
        }

        super.pronounce = pronounce;
        this.setHtml(doc.html());
    }

    private static String stringToHtml(String input) throws IOException {
        BufferedReader buffer = new BufferedReader(new StringReader(input));
        StringBuilder output = new StringBuilder();

        String wordAndPronounce = buffer.readLine();
        if (wordAndPronounce == null || wordAndPronounce.charAt(0) != '@') {
            throw new IOException("Word not found. Must start with \"@\".");
        }

        // Define the regex pattern
        Pattern pattern = Pattern.compile("@([^/]+) /([^/]+)/");
        // Create a Matcher object
        Matcher matcher = pattern.matcher(input);
        // Search for the pattern
        if (matcher.find()) {
            // Get the text between "@" and "/"
            String word = matcher.group(1);
            if (word.isBlank() || word.isEmpty()) {
                throw new IOException("Empty word.");
            }
            output.append("<h1>").append(word.trim()).append("</h1>");
            // Get the text between "/" and the next "/"
            output.append("<h3><i>/").append(matcher.group(2).trim()).append("/</i></h3>");
        } else {
            throw new IOException("Input must be in the format of @word /pronunciation/");
        }

        String line = "";
        boolean l2ListOpened = false;
        boolean l3ListOpened = false;
        while ((line = buffer.readLine()) != null) {
            if (line.charAt(0) == '*') {
                if (l3ListOpened) {
                    l3ListOpened = false;
                    output.append("</ul>");
                }
                if (l2ListOpened) {
                    l2ListOpened = false;
                    output.append("</li>").append("</ul>");
                }
                output.append("<h2>").append(line.substring(1)).append("</h2>");
            } else if (line.charAt(0) == '-') {
                if (l3ListOpened) {
                    output.append("</ul>").append("</li>");
                    l3ListOpened = false;
                }
                if (!l2ListOpened) {
                    l2ListOpened = true;
                    output.append("<ul>");
                }
                output.append("<li>").append(line.substring(1));
            } else if (line.charAt(0) == '=') {
                if (!l3ListOpened) {
                    l3ListOpened = true;
                    output.append("<ul style=\"list-style-type:circle\">");
                }
                int italicPosition = line.indexOf('+');

                if (italicPosition == -1) {
                    italicPosition = line.length();
                } else {
                    italicPosition++;
                }
                output.append("<li>")
                        .append(line, 1, italicPosition - 1)
                        .append(":<i>")
                        .append(line, italicPosition, line.length())
                        .append("</i>")
                        .append("</li>");
            } else {
                output.append("<p>").append(line).append("/p");
            }
        }

        if (l3ListOpened) {
            l3ListOpened = false;
            output.append("</ul>");
        }
        if (l2ListOpened) {
            l2ListOpened = false;
            output.append("</li>").append("</ul>");
        }

        return output.toString();
    }

    private static String htmlToString(String input) {
        Document doc = Jsoup.parse(input);
        Elements elements = doc.body().children();
        StringBuilder output = new StringBuilder();

        for (Element element : elements) {
            if (element.tagName().equals("h1")) {
                output.append("@").append(element.text());
            } else if (element.tagName().equals("h3")) {
                output.append(" ").append(element.text()).append("\n");
            } else if (element.tagName().equals("h2")) {
                output.append("* ").append(element.text()).append("\n");
            } else if (element.tagName().equals("ul")) {
                parseList(element, output, 1);
            }
        }

        return output.toString();
    }

    private static void parseList(Element ul, StringBuilder output, int level) {
        for (Element li : ul.children()) {
            if (level <= 2) {
                String prefix = level == 1 ? "-" : "=";
                output.append(prefix).append(" ").append(li.ownText()).append("\n");
            }
            if (!li.children().isEmpty() && li.child(0).tagName().equals("ul")) {
                parseList(li.child(0), output, level + 1);
            }
        }
    }

    private static String getWord(String html) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.body().children();

        for (Element element : elements) {
            if (element.tagName().equals("h1")) {
                return element.text();
            }
        }

        return null;
    }

    public static String getPronounce(String html) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.body().children();

        for (Element element : elements) {
            if (element.tagName().equals("h3")) {
                return element.text();
            }
        }

        return null;
    }
}
