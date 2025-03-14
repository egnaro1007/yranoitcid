package com.yranoitcid.backend.dictionary;

import com.yranoitcid.backend.database.DatabaseQuery;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

public class Dictionary {

    private static Dictionary instance;

    private String databaseFilePath;
    private HashMap<SimpleEntry<String, String>, String> tableList = new HashMap<>();
    private final DatabaseQuery database = new DatabaseQuery();

    protected Dictionary(String dbPath) {
        try {
            database.setDatabaseFilePath(dbPath);
            this.databaseFilePath = dbPath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the instance of the Dictionary. If the instance is not initialized, initialize it with
     * dbPath. If the instance is already initialized, check if the dbPath is the same as the
     * current one. If not, throw an exception.
     *
     * @param dbPath The path to the database file.
     * @return The instance of the Dictionary.
     * @throws RuntimeException If the Dictionary is already initialized with a different dbPath.
     */
    public static Dictionary getInstance(String dbPath) {
        if (instance == null) {
            instance = new Dictionary(dbPath);
            return instance;
        } else if (instance.getDatabaseFilePath().equals(dbPath)) {
            return instance;
        } else {
            throw new RuntimeException("Dictionary already initialized");
        }
    }

    /**
     * Get the instance of the Dictionary. If the instance is not initialized, throw an exception.
     * Use {@link #getInstance(String dbPath)} to initialize the Dictionary.
     *
     * @return The instance of the Dictionary.
     * @throws RuntimeException If the Dictionary is not initialized.
     */
    public static Dictionary getInstance() {
        if (instance == null) {
            throw new RuntimeException("Dictionary not initialized");
        }
        return instance;
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
        tableList.put(new SimpleEntry<>(srcLang, destLang), tableName);
    }

    /**
     * Search for words that contain the term.
     *
     * @param srcLang    Source language.
     * @param destLang   Destination language.
     * @param searchTerm The term to search for.
     * @return An ArrayList of Word object that contain the result. The result is ordered by the
     * position of the term in the Word.
     * @throws Exception If the Word is not found.
     */
    public ArrayList<Word> searchContains(String srcLang, String destLang, String searchTerm)
            throws Exception {
        ArrayList<Word> result = searchTermInDatabase(srcLang, destLang, searchTerm, false);
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
     * @throws Exception If the Word is not found.
     */
    public Word searchExact(String srcLang, String destLang, String searchTerm) throws Exception {
        ArrayList<Word> result = searchTermInDatabase(srcLang, destLang, searchTerm, true);
//        for (Word w : result) {
//            if (w.getWord().equals(searchTerm)) {
//                return w;
//            }
//        }
//        return null;
        // throw new Exception("Word not found");
        return result.isEmpty() ? null : result.get(0);
    }

    private ArrayList<Word> searchTermInDatabase(
            String srcLang,
            String destLang,
            String searchTerm,
            boolean searchExact) {
        String tableName = tableList.get(new SimpleEntry<>(srcLang, destLang));
        ArrayList<Word> result = new ArrayList<>();

        try (ResultSet resultSet = database.query(tableName, "word", searchTerm, searchExact)) {
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                String html = resultSet.getString("html");
                String pronounce = resultSet.getString("pronounce");
                String description = resultSet.getString("description");

                result.add(new Word(word, html, description, pronounce));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public ArrayList<Word> getRandomWords(String srcLang, String destLang, Integer
            numberOfRandom) {
        String tableName = tableList.get(new SimpleEntry<>(srcLang, destLang));
        if (tableName == null) {
            throw new RuntimeException("Table not found");
        }

        ArrayList<Word> result = new ArrayList<>();

        try (ResultSet resultSet = database.randomQuery(tableName, numberOfRandom)) {
            while (resultSet.next()) {
                String word = resultSet.getString("Word");
                String html = resultSet.getString("html");
                String pronounce = resultSet.getString("pronounce");
                String description = resultSet.getString("description");

                result.add(new Word(word, html, description, pronounce));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public Word getRandomWord(String srcLang, String destLang) {
        return getRandomWords(srcLang, destLang, 1).get(0);
    }

    public void setLimit(Integer limit) {
        database.setLimitQuery(limit);
    }

    public void addWord(String srcLang, String destLang, Word newWord) {
        // Check if the word already exists
        try {
            Word validate = searchExact(srcLang, destLang, newWord.getWord());
            if (validate != null) {
                throw new RuntimeException("Word already exists");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Get the table name
        String tableName = tableList.get(new SimpleEntry<>(srcLang, destLang));
        if (tableName == null) {
            throw new RuntimeException("Table not found");
        }

        // Prepare the word
        String word = newWord.getWord();
        String html = newWord.getHtml();
        String description = newWord.getDescription();
        String pronounce = newWord.getPronounce();

        // Prepare database query
        HashMap<String, String> newRecord = new HashMap<>();
        newRecord.put("word", word);
        newRecord.put("html", html);
        newRecord.put("description", description);
        newRecord.put("pronounce", pronounce);
        // Perform the query
        database.insert(tableName, newRecord);
    }

    public void removeWord(String srcLang, String destLang, String word) {
        String tableName = tableList.get(new SimpleEntry<>(srcLang, destLang));
        if (tableName == null) {
            throw new RuntimeException("Table not found");
        }
        database.delete(tableName, "word", word);
    }
}
