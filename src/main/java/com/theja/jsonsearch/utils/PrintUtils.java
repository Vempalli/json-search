package com.theja.jsonsearch.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class PrintUtils {
    /**
     * Using gson builder print the json in pretty format
     */
    public static void printPretty(JsonObject jsonObject) {
        if (jsonObject != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            print(gson.toJson(jsonObject));
        }
    }

    /**
     * In future if we decide to print to some file rather than console,
     * we can just change this method
     */
    public static void print(String text) {
        System.out.println(text);
    }
}
