package com.yranoitcid.backend.api;

import com.yranoitcid.backend.dictionary.Word;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.parser.ParseException;

import com.yranoitcid.backend.database.DatabaseQuery;

public class test {

   public static void main(String[] args) throws IOException {
//        googleTranslate tran = new googleTranslate();
//        System.out.println(tran.search("hello"));

       GoogleChan translate = new GoogleChan();
       // System.out.println(translate.getUri());
       try {
           System.out.println(translate.search("A cup of tea"));
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

    // public static void main(String[] args) {

    //     databaseQuery dict = new databaseQuery("dict.db");
    //     ResultSet resultSet = dict.randomQuery("av", 10);
    //     while(true) {
    //         try {
    //             if (!resultSet.isAfterLast()) {
    //                 System.out.println(resultSet.getString("word"));
    //                 resultSet.next();
    //             } else {
    //                 break;
    //             }
    //         } catch (SQLException e) {
    //             throw new RuntimeException(e);
    //         }

    //     }
    // }
}
