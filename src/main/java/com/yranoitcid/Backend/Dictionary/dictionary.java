package com.yranoitcid.Backend.Dictionary;

import com.yranoitcid.Backend.Database.databaseQuery;
import com.yranoitcid.Backend.Dictionary.word;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

public class dictionary {

    private String dbPath;
    private HashMap<Pair<String, String>, String> tableList = new HashMap<Pair<String, String>, String>();

    private databaseQuery database = new databaseQuery();

    public dictionary(String dbPath) {
        try {
            database.setDatabaseFilePath(dbPath);
            this.dbPath = dbPath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Each translation in the database is stored in a table.
     *
     * @param srcLang The source language.
     * @param destLang The destination language.
     * @param tableName The name of the table.
     */
    public void initTable(String srcLang, String destLang, String tableName) {
        tableList.put(new Pair<String, String>(srcLang, destLang), tableName);
    }


    /**
     *
     * @param srcLang
     * @param destLang
     * @param searchTerm
     * @param mode The mode to query.
     *             'f' for full word match.
     *             'c' for contains.
     * @return
     */
    public ArrayList<word> search(String srcLang, String destLang, String searchTerm, Character mode) {
        String tableName = tableList.get(new Pair<String, String>(srcLang, destLang));
        ArrayList<word> result = new ArrayList<>();

        try {
            ResultSet resultSet = database.query(tableName, "word", searchTerm);
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
}
