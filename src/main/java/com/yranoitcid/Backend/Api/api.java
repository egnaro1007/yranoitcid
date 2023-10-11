package com.yranoitcid.Backend.Api;

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
    private StringBuilder response = new StringBuilder();

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
     * Start call api. When call api, the response will be stored in response variable and override
     * the old one. This response can be get by getResponseAsString() method.
     *
     * @return the connection.
     */
    public HttpsURLConnection connect() {
        responseClear();
        try {
            URL url = new URL(getUri());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("User-Agent",
                    userAgents.get((int) Math.round(Math.random() * (userAgents.size() - 1))));

            System.out.println(url.toString());
            connection.connect();

            // System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                // Read response as String
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // System.out.println(response);

                // Return the connection
                return connection;
            } else {
                System.out.println(connection.getResponseCode());
                throw (new Exception("Connection fail"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object parse() throws ParseException;


    /**
     * Get response of previous call api as String.
     *
     * @return A String of response.
     */
    public String getResponseAsString() {
        return response.toString();
    }

    /**
     * Clear the previous response.
     */
    public void responseClear() {
        response.setLength(0);
    }

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

    /** Clear the pragmas list. */
    public void clearPragmas() {
        pragmas.clear();
    }
}
