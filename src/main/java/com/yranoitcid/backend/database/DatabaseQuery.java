package com.yranoitcid.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.sqlite.Function;


/**
 * database query class for SQLite database. database file path can only be set once and can't be
 * changed. Set the database file path in the constructor or by calling setDatabaseFilePath method.
 */
public class DatabaseQuery {

    private String dbFilePath = "";
    private Connection connection = null;
    private Integer limitQuery = 15;
    private boolean isInit = false;

    public DatabaseQuery() {
    }

    public DatabaseQuery(String p_dbFilePath) {
        setDatabaseFilePath(p_dbFilePath);
    }

    /**
     * Set the database file path database can only be initialized once and can't be changed. If you
     * want to change the database file path, create a new DatabaseQuery object.
     *
     * @param p_dbFilePath The path to the database file
     */
    public void setDatabaseFilePath(String p_dbFilePath) {
        if (isInit) {
            throw new RuntimeException("database is already initialized");
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
     * database query with "LIKE". Order by the position of the search term in the column.
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
                + "ORDER BY CHARINDEX(?, " + column + ")"
                + " LIMIT " + limitQuery.toString();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set the parameters
            preparedStatement.setString(1, "%" + term + "%");
            preparedStatement.setString(2, term);
            // Execute the query and get the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result set if the Word is found
            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Not found");
            }
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ResultSet queryWithLevenshteinDistance(String table, String column, String term, int maxLevenshteinDistance) throws SQLException {

        // Register the custom Levenshtein function with the SQLite database
        Function.create(connection, "LEVENSHTEIN", new LevenshteinFunction());

        String sql = "SELECT * FROM " + table
                + " WHERE LENGTH(" + column + ") BETWEEN ? AND ?"
                + " AND LEVENSHTEIN(" + column + ", ?) <= ?"
                + " ORDER BY"
                            + " CASE WHEN " + column + " LIKE ? || '%' THEN 1 ELSE 2 END"
                            + ", LEVENSHTEIN(" + column + ", ?)"
                            + ", " + column
                + " LIMIT " + limitQuery.toString();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, term.length() - maxLevenshteinDistance);
            preparedStatement.setInt(2, term.length() + maxLevenshteinDistance);
            preparedStatement.setString(3, term);
            preparedStatement.setInt(4, maxLevenshteinDistance);
            preparedStatement.setString(5, term);
            preparedStatement.setString(6, term);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Not found");
            }

            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet randomQuery(String table, Integer magicNumber) {
        String sql = "SELECT *"
                + " FROM " + table
                + " ORDER BY RANDOM()"
                + " LIMIT " + magicNumber;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Execute the query and get the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result set if the Word is found
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
            // Process the result set if the Word is found
            if (!resultSet.isBeforeFirst()) {
                throw new RuntimeException("Not found");
            }
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void delete(String table, String column, String term) {
        String sql = "DELETE"
                + " FROM " + table
                + " WHERE " + column + " = "
                + "\"" + term + "\"";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            System.out.println(preparedStatement.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String table, HashMap<String, String> newRecord) {
        if (newRecord.isEmpty()) {
            return; // Do not proceed if there's no data
        }

        StringBuilder fields = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String key : newRecord.keySet()) {
            fields.append(key).append(",");
            placeholders.append("?,");
        }

        // Remove the trailing comma
        fields.setLength(fields.length() - 1);
        placeholders.setLength(placeholders.length() - 1);

        String sql = "INSERT INTO " + table + " (" + fields.toString() + ") VALUES ("
                + placeholders.toString() + ")";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int index = 1;
            for (String value : newRecord.values()) {
                preparedStatement.setString(index++, value);
            }

            System.out.println(preparedStatement.executeUpdate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public void insert(String table, HashMap<String, String> newRecord) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("INSERT INTO ");
//        sql.append(table);
//        sql.append(" ");
//        sql.append("(");
//        for (String key : newRecord.keySet()) {
//            sql.append(key);
//            sql.append(",");
//        }
//        sql.deleteCharAt(sql.length() - 1);
//        sql.append(")");
//        sql.append(" VALUES ");
//        sql.append("(");
//        for (String value : newRecord.values()) {
//            sql.append("\"");
//            sql.append(value);
//            sql.append("\"");
//            sql.append(",");
//        }
//        sql.deleteCharAt(sql.length() - 1);
//        sql.append(")");
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
//            System.out.println(preparedStatement.executeUpdate());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

// INSERT INTO nguoidan (Hovaten, Ngaysinh, Gioitinh, Sdt)
// value ("Ten", "2023-12-11", "Bisexual", "001");


    public Integer getLimitQuery() {
        return limitQuery;
    }

    public void setLimitQuery(Integer limitQuery) {
        this.limitQuery = limitQuery;
    }

    public String where() {
        return dbFilePath;
    }
}
