package com.yranoitcid.Backend.Dictionary;

import java.util.ArrayList;
import java.util.HashMap;

public class test {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        dictionary dict = new dictionary("dict.db");
        dict.initTable("en", "vi", "av");
        ArrayList<word> result = dict.search("en", "vi", "ping",'c');
        for (int i = 0; i < 10; i++) {
            word w = result.get(i);
            System.out.println(w);
        }
    }
}