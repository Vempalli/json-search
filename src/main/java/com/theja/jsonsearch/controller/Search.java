package com.theja.jsonsearch.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.theja.jsonsearch.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This utils class holds all the methods that are required for searching the required JSON
 */
public class Search {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CACHE_EXPIRE_TIME = 10;
    private static final int MAXIMUM_CACHE_ENTRIES = 3; // we only have users, orgs and ticket objects

    /**
     * Since all objects in same category has same fields in their JSON, we can speed up multiple searches
     * by just caching the list of fields. We would rarely add/delete fields so it makes sense to cache
     * The cache is evicted based on time. Currently it's set to 10 min this value can be made dynamic
     * and the size of cache is just set to 10 (but there are just 3 objects in our case)
     */
    private static LoadingCache<String/*category_name*/, Set<String>/*all available fields*/> searchableFields = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_CACHE_ENTRIES)
            .expireAfterWrite(CACHE_EXPIRE_TIME, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Set<String>>() {
                        public Set<String> load(@Nonnull String optedCategory) {
                            JsonObject jsonObject = new JsonObject();
                            try {
                                jsonObject = readFieldsFromAnyObjectOnCategory(optedCategory);
                            }
                            catch (IOException ex) {
                                LOGGER.error(
                                        String.format("Cannot retrieve the fields from first object from %s json file",
                                                optedCategory), ex);
                            }
                            return jsonObject.keySet();
                        }
                    });
    /**
     * This is main implementation of search. We use Gson stream reader
     * Since the JSON would be too big to hold in memory (by building an object model), we need to do stream processing
     * This search returns exact and also partial results. This is achieved by converting the json field to string
     * and perform string search operations
     * This method returns list of all objects that matches the given search criteria
     * @param optedCategory defines the category on which search has to be performed
     *                      currently we have 3 types, organizations, tickets and users
     * @param optedSearchTerm The name of the field on which search is to be performed
     * @param optedSearchValue The search string that client is interested in
     */
    public static List<JsonObject> performSearchOperation(String optedCategory,
                                                          String optedSearchTerm,
                                                          String optedSearchValue) throws IOException {
        String errMsg = validateSearchParms(optedCategory, optedSearchTerm, optedSearchValue);
        if(StringUtils.isNotEmpty(errMsg)) {
            LOGGER.info("Search cannot be performed because of invalid search parameters");
            throw new IllegalArgumentException(errMsg);
        }
        List<JsonObject> searchResult = new ArrayList<>();
        // Stream process the required json file
        InputStream jsonStream = JsonUtils.getJsonStreamFromFile(optedCategory);
        Gson gson = new Gson();
        try(JsonReader streamReader = new JsonReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8.name()))){
            streamReader.beginArray();
            while (streamReader.hasNext()) {
                // Perform string search on selected json field that is currently being stream processed
                JsonObject jsonObject = gson.fromJson(streamReader, JsonObject.class);
                String jsonAsString = getStringRepresentation(jsonObject, optedSearchTerm);
                if (jsonAsString.contains(optedSearchValue)) {
                    searchResult.add(jsonObject);
                }
                if(StringUtils.isEmpty(optedSearchValue) && StringUtils.isEmpty(jsonAsString)) {
                    searchResult.add(jsonObject);
                }
            }
            streamReader.endArray();
        }
        return searchResult;
    }

    /**
     * Make sure all the parms are present to perform search operation
     */
    private static String validateSearchParms(String optedCategory,
                                              String optedSearchTerm,
                                              String optedSearchValue) {
        StringBuilder errMsg = new StringBuilder();
        if (StringUtils.isEmpty(optedCategory)) {
            errMsg.append("Search category can't be empty.");
        }
        if (StringUtils.isEmpty(optedSearchTerm)) {
            errMsg.append(" Search category can't be empty.");
        }
        if (optedSearchValue == null) {
            errMsg.append(" Search value can't be null.");
        }
        return errMsg.toString();
    }

    /**
     * Gets the string representation of required field from given json object
     */
    private static String getStringRepresentation(JsonObject jsonObject, String optedSearchTerm) {
        String stringRepresentation = StringUtils.EMPTY;
        JsonElement jsonElement = jsonObject.get(optedSearchTerm);
        if (jsonElement == null) {
            return stringRepresentation;
        }
        if (jsonElement.isJsonPrimitive()) {
            stringRepresentation = jsonElement.getAsString();
        }
        return stringRepresentation;
    }

    /**
     * Gets the list of all searchable fields based on category type selected
     */
    public static Set<String> getAvailableSearchableFieldsOnCategory(String optedCategory) throws Exception {
        return searchableFields.get(optedCategory);
    }

    /**
     * This method returns the list of all keys in the any JSON object of selected category
     * Note: Assumption is made that all the field in specific json file will have same fields
     * Because of above assumption we just retrieve the fields from first object in the file
     */
    private static JsonObject readFieldsFromAnyObjectOnCategory(String optedCategory) throws IOException {
        Gson gson = new Gson();
        InputStream jsonStream = JsonUtils.getJsonStreamFromFile(optedCategory);
        JsonObject jsonObject = null;
        try(JsonReader streamReader = new JsonReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8.name()))){
            streamReader.beginArray();
            if (streamReader.hasNext()) {
                jsonObject = gson.fromJson(streamReader, JsonObject.class);
            }
        }
        return jsonObject;
    }
}
