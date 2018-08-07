package com.theja.jsonsearch.utils;

import com.google.gson.JsonObject;
import com.theja.jsonsearch.controller.SearchCategoryDependencies;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This Util class holds all the necessary helper
 * methods to work on gson's JSON object
 */
public class JsonUtils {
    private static final Logger LOGGER = LogManager.getLogger();
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
            LOGGER.debug(String.format("unable to read from target stream which is null for %s", optedCategory));
            throw new IllegalStateException(String.format("Unable to generate stream from %s.json", optedCategory));
        }
        return targetStream;
    }

    /**
     * Once the search is performed this method prints the matched results and
     * also appends the details about related fields for each of matched result
     */
    public static void printResults(String optedCategory, List<JsonObject> results) {
        if (CollectionUtils.isEmpty(results)) {
            PrintUtils.print("No results found");
        }
        results.forEach(jsonObject -> {
            try {
                jsonObject = SearchCategoryDependencies.appendRelatedFields(optedCategory, jsonObject);
            } catch (IOException ex) {
                LOGGER.error(String.format("Can't retrieve related fields for %s", optedCategory), ex);
            }
            PrintUtils.printPretty(jsonObject);
        });
    }


}
