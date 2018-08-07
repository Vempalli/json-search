package com.theja.jsonsearch.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.theja.jsonsearch.controller.SearchCategoryDependencies;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This Util class holds all the necessary helper
 * methods to work on gson's JSON object
 */
public class JsonUtils {

    private static final String JSON_FILE_EXTENSION = ".json";


    /**
     * This method returns the JSON object as input stream
     * @param optedCategory used to identify which JSON object to process for search
     */
    public static InputStream getJsonStreamFromFile(String optedCategory) {
        Class clazz = JsonUtils.class;
        InputStream targetStream =
                clazz.getResourceAsStream(File.separator + optedCategory + JSON_FILE_EXTENSION);
        if (targetStream == null) {
            throw new IllegalStateException(String.format("Unable to generate stream from %s.json", optedCategory));
        }
        return targetStream;
    }

    public static void printResults(String optedCategory, List<JsonObject> results) {
        if (CollectionUtils.isEmpty(results)) {
            System.out.println("No results found");
        }
        results.forEach(jsonObject -> {
            try {
                jsonObject = SearchCategoryDependencies.appendRelatedFields(optedCategory, jsonObject);
            } catch (IOException ex) {
                System.out.println(String.format("Can't retrieve related fields for %s", optedCategory));
            }
            printPretty(jsonObject);
        });
    }

    private static void printPretty(JsonObject jsonObject) {
        if (jsonObject != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(jsonObject));
        }
    }

}
