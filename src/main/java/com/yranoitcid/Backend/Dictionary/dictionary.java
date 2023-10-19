package com.yranoitcid.Backend.Dictionary;

import com.yranoitcid.Backend.Database.databaseQuery;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

public class dictionary {

    private String databaseFilePath;
    private HashMap<Pair<String, String>, String> tableList = new HashMap<>();
    private databaseQuery database = new databaseQuery();

    public dictionary(String dbPath) {
        try {
            database.setDatabaseFilePath(dbPath);
            this.databaseFilePath = dbPath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getDatabaseFilePath() {
        return databaseFilePath;
    }

    /**
     * Each translation in the database is stored in a table.
     *
     * @param srcLang   The source language.
     * @param destLang  The destination language.
     * @param tableName The name of the table.
     */
    public void initTable(String srcLang, String destLang, String tableName) {
        tableList.put(new Pair<>(srcLang, destLang), tableName);
    }

    /**
     * Search for words that contain the term.
     *
     * @param srcLang    Source language.
     * @param destLang   Destination language.
     * @param searchTerm The term to search for.
     * @return An ArrayList of Word object that contain the result. The result is ordered by the
     * position of the term in the word.
     * @throws Exception If the word is not found.
     */
    public ArrayList<word> searchContains(String srcLang, String destLang, String searchTerm)
            throws Exception {
        ArrayList<word> result = searchTermInDatabase(srcLang, destLang, searchTerm);
        if (result.isEmpty()) {
            // throw new Exception("Word not found");
        }
        return result;
    }

    /**
     * Search for words that exactly match the term.
     *
     * @param srcLang    Source language.
     * @param destLang   Destination language.
     * @param searchTerm The term to search for.
     * @return A Word object.
     * @throws Exception If the word is not found.
     */
    public word searchExact(String srcLang, String destLang, String searchTerm) throws Exception {
        ArrayList<word> result = searchTermInDatabase(srcLang, destLang, searchTerm);
        for (word w : result) {
            if (w.getWord().equals(searchTerm)) {
                return w;
            }
        }
        return null;
        // throw new Exception("Word not found");
    }

    private ArrayList<word> searchTermInDatabase(
            String srcLang,
            String destLang,
            String searchTerm) {
        String tableName = tableList.get(new Pair<>(srcLang, destLang));
        ArrayList<word> result = new ArrayList<>();

        try (ResultSet resultSet = database.query(tableName, "word", searchTerm)) {
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                String html = resultSet.getString("html");
                String pronounce = resultSet.getString("pronounce");
                String description = resultSet.getString("description");

                result.add(new word(word, html, description, pronounce));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public ArrayList<word> getRandomWords(String srcLang, String destLang, Integer
            numberOfRandom) {
        String tableName = tableList.get(new Pair<>(srcLang, destLang));
        if (tableName == null) {
            throw new RuntimeException("Table not found");
        }

        ArrayList<word> result = new ArrayList<>();

        try (ResultSet resultSet = database.randomQuery(tableName, numberOfRandom)) {
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                String html = resultSet.getString("html");
                String pronounce = resultSet.getString("pronounce");
                String description = resultSet.getString("description");

                result.add(new word(word, html, description, pronounce));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public word getRandomWord(String srcLang, String destLang) {
        return getRandomWords(srcLang, destLang, 1).get(0);
    }

    public void setLimit(Integer limit) {
        database.setLimitQuery(limit);
    }
}
