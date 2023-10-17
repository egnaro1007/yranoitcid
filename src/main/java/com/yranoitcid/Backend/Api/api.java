package com.yranoitcid.Backend.Api;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import javafx.util.Pair;
import java.util.Iterator;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.parser.ParseException;

import static java.net.URLEncoder.encode;

abstract public class api {

    protected final String endPoint;
    protected ArrayList<Pair<String, String>> pragmas = new ArrayList<>();
    protected ArrayList<String> userAgents = new ArrayList<>();
    protected HttpsURLConnection connection = null;

    public api(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getUri() {
        StringBuilder uri = new StringBuilder();
        uri.append(endPoint);
        uri.append("?");

        for (Pair<String, String> pragma : pragmas) {
            String newPragma = encode(pragma.getKey()) + "=" + encode(pragma.getValue()) + "&";
            uri.append(newPragma);
        }

//        for(Map.Entry<String, String> pragma : pragmas.entrySet()) {
//            String newPragma = encode(pragma.getKey()) + "=" + encode(pragma.getValue()) + "&";
//            uri.append(newPragma);
//        }
        uri.deleteCharAt(uri.length() - 1);
        return uri.toString();
    }

    /**
     * Start call api. When call api, store in connection variable.
     */
    protected void connect() {
        try {
            URL url = new URL(getUri());
            connection = (HttpsURLConnection) url.openConnection();

            if (!userAgents.isEmpty()) {
                connection.setRequestProperty("User-Agent",
                        userAgents.get((int) Math.round(Math.random() * (userAgents.size() - 1))));
            }

//            System.out.println(url.toString());

            // System.out.println(connection.getResponseCode());
            switch (connection.getResponseCode()) {
                case HttpsURLConnection.HTTP_OK -> {
                    // Return the connection
                    // return connection;
                }
                default -> {
                    System.out.println(connection.getResponseCode());
                    throw (new Exception("Connection fail"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object parse() throws Exception;

//    /**
//     * Get response of previous call api as String.
//     *
//     * @return A String of response.
//     */
//    public String getResponseAsString() {
//        return response.toString();
//    }

    /**
     * Add a user agent to the user agents list. The user agent will be randomly selected when call
     * api.
     *
     * @param newUserAgent A string that represent a user agent.
     */
    public void addUserAgents(String newUserAgent) {
        userAgents.add(newUserAgent);
    }

    /**
     * Add a list of user agents to the user agents list. The user agent will be randomly selected
     * when call api.
     *
     * @param newUserAgents A list of string that represent a user agent.
     */
    public void addUserAgents(String[] newUserAgents) {
        userAgents.addAll(Arrays.asList(newUserAgents));
    }

    /**
     * Clear the user agents list.
     */
    public void clearUserAgents() {
        userAgents.clear();
    }

    /**
     * Add a pragma to the pragmas list. The pragma need to be a pair of Pragma and Value.
     *
     * @param newPragma A string that represent a pragma.
     * @param value     A string that represent a value of that pragma.
     */
    public void addPragma(String newPragma, String value) {
        pragmas.add(new Pair<>(newPragma, value));
    }

    /**
     * Delete a pragma.
     *
     * @param deletePragma A string that represent a pragma the need to be deleted.
     */
    public void deletePragma(String deletePragma) {
        pragmas.removeIf(pragma -> pragma.getKey().equals(deletePragma));
    }

    public void editPragma(String editPragma, String newValue) throws RuntimeException {
        Iterator<Pair<String, String>> iterator = pragmas.iterator();
        while (iterator.hasNext()) {
            Pair<String, String> pragma = iterator.next();
            if (pragma.getKey().equals(editPragma)) {
                iterator.remove();
                pragmas.add(new Pair<>(editPragma, newValue));
                return;
            }
            // throw new RuntimeException("Pragma not found");
        }
    }

    /**
     * Clear the pragmas list.
     */
    public void clearPragmas() {
        pragmas.clear();
    }
}
