package com.yranoitcid.Backend.Api;

import com.yranoitcid.Backend.Dictionary.word;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.parser.ParseException;

import com.yranoitcid.Backend.Database.databaseQuery;

public class test {

//    public static void main(String[] args) {
////        googleTranslate tran = new googleTranslate();
////        System.out.println(tran.search("hello"));
//
//        googleChan translate = new googleChan();
//        // System.out.println(translate.getUri());
//        try {
//            translate.search("Hello");
//            System.out.println(translate.parse());
//            translate.search("A cup of tea");
//            System.out.println(translate.parse());
//
//            translate.setLanguage('d', "en");
//            translate.search("コニチワ");
//            System.out.println(translate.parse());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void main(String[] args) {

        databaseQuery dict = new databaseQuery("dict.db");
        ResultSet resultSet = dict.randomQuery("av", 10);
        while(true) {
            try {
                if (!resultSet.isAfterLast()) {
                    System.out.println(resultSet.getString("word"));
                    resultSet.next();
                } else {
                    break;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
