package com.yranoitcid.Backend.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database query class for SQLite database. Database file path can only be set once and can't be
 * changed. Set the database file path in the constructor or by calling setDatabaseFilePath method.
 */
public class databaseQuery {

    private String dbFilePath = "";
    private Connection connection = null;

    private boolean isInit = false;

    public databaseQuery() {
    }

    public databaseQuery(String p_dbFilePath) {
        setDatabaseFilePath(p_dbFilePath);
    }

    /**
     * Set the database file path Database can only be initialized once and can't be changed. If you
     * want to change the database file path, create a new databaseQuery object.
     *
     * @param p_dbFilePath The path to the database file
     */
    public void setDatabaseFilePath(String p_dbFilePath) {
        if (isInit) {
            throw new RuntimeException("Database is already initialized");
        }

        dbFilePath = p_dbFilePath;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            // If the connection is successful, set isInit to true
            isInit = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Database query with "LIKE". Order by the position of the search term in the column.
     *
     * @param table  The table to query.
     * @param column The column to query.
     * @param term   The term to query.
     * @return a ResultSet object containing rows that match the query.
     */
    public ResultSet query(String table, String column, String term) {
        String sql = "SELECT * "
                + "FROM " + table
                + " WHERE " + column
                + " LIKE ?"
                + "ORDER BY CHARINDEX(?, " + column + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set the parameters
            preparedStatement.setString(1, "%" + term + "%");
            preparedStatement.setString(2, term);
            // Execute the query and get the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result set if the word is found
            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Not found");
            }
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ResultSet backup(String table, String column, String term) {
        String sql = "SELECT * "
                + "FROM " + table
                + " WHERE " + column
                + " LIKE ?"
                + "ORDER BY CHARINDEX(?, " + column + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set the parameters
            preparedStatement.setString(1, "%" + term + "%");
            preparedStatement.setString(2, term);
            // Execute the query and get the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result set if the word is found
            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Not found");
            }
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String where() {
        return dbFilePath;
    }
}
