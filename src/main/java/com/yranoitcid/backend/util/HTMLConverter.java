package com.yranoitcid.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLConverter {

    public static String stringToHtml(String input) throws IOException {
        BufferedReader buffer = new BufferedReader(new StringReader(input));
        StringBuilder output = new StringBuilder();

        String wordAndPronounce = buffer.readLine();
        if (wordAndPronounce == null || wordAndPronounce.charAt(0) != '@') {
            throw new IOException("Word not found. Must start with \"@\".");
        }

        int startPronounceIndex = wordAndPronounce.indexOf('/') + 1;

        String word = "";
        String pronounce = "";

        if (startPronounceIndex != 0) {
            word = wordAndPronounce.substring(1, startPronounceIndex - 2).trim();

            int endPronounceIndex = wordAndPronounce.indexOf('/', startPronounceIndex);
            if (endPronounceIndex != -1) {
                pronounce = wordAndPronounce.substring(startPronounceIndex, endPronounceIndex)
                        .trim();
            }
        } else {
            word = wordAndPronounce.substring(1).trim();
        }

        output.append("<h1>").append(word).append("</h1>");
        if (!pronounce.isEmpty()) {
            output.append("<h3><i>/").append(pronounce).append("/</i></h3>");
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

    public static String htmlToString(String input) {
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

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println(HTMLConverter.stringToHtml(scan.nextLine()));
    }
}
