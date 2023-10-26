package com.yranoitcid.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLConverter {

    public static String stringToHtml(String input) throws IOException {
        BufferedReader buffer = new BufferedReader(new StringReader(input));
        StringBuilder output = new StringBuilder();

        String wordAndPronounce = buffer.readLine();
        if (wordAndPronounce != null && wordAndPronounce.charAt(0) == '@') {
            // Define the regular expression pattern
            Pattern pattern = Pattern.compile("@([^/]+) /([^/]+)/");

            // Create a Matcher object
            Matcher matcher = pattern.matcher(input);

            // Search for the pattern
            if (matcher.find()) {
                // Get the text between "@" and "/"
                output.append("<h1>").append(matcher.group(1)).append("</h1>");
                // Get the text between "/" and the next "/"
                output.append("<h3><i>/").append(matcher.group(2)).append("/</i></h3>");
            } else {
                throw new IOException("Invalid input format");
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
                    output.append("<li>")
                            .append(line, 1, italicPosition)
                            .append(":<i>")
                            .append(line, italicPosition + 1, line.length())
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
        } else {
            throw new IOException();
        }
        // output.append("</ul>");

        return output.toString();
    }

    public String htmlToString(String input) {
        return "";
    }

    /**
     * Convert inputs of word into a single html paragraph.
     */
    public String generateHtml() {
        return "";
    }
}
