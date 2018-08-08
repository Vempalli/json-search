package com.theja.jsonsearch.controller;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SearchTest {

    @Test
    public void performSuccessfulSearchOperationOnUsersCategory() throws IOException {
        String optedCategory = "users";
        String optedField = "_id";
        String searchValue = "71";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(),1);
        JsonObject searchMatch = result.get(0);
        Assert.assertEquals(searchMatch.get("name").getAsString(), "Prince Hinton");
    }

    @Test
    public void performSuccessfulSearchOperationOnOrganizationCategory() throws IOException {
        String optedCategory = "organizations";
        String optedField = "name";
        String searchValue = "Enthaze";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(),1);
        JsonObject searchMatch = result.get(0);
        Assert.assertEquals(searchMatch.get("_id").getAsString(), "101");
    }

    @Test
    public void performSuccessfulSearchOperationOnTicketsCategory() throws IOException {
        String optedCategory = "tickets";
        String optedField = "subject";
        String searchValue = "A Catastrophe in Micronesia";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(),1);
        JsonObject searchMatch = result.get(0);
        Assert.assertEquals(searchMatch.get("_id").getAsString(), "1a227508-9f39-427c-8f57-1b72f3fab87c");
    }

    @Test
    public void performUnSuccessfulSearchOperationOnUsersCategory() throws IOException {
        String optedCategory = "users";
        String optedField = "_id";
        String searchValue = "9999";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void performUnSuccessfulSearchOperationOnOrganizationCategory() throws IOException {
        String optedCategory = "organizations";
        String optedField = "_id";
        String searchValue = "unit-test-123";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void performUnSuccessfulSearchOperationOnTicketsCategory() throws IOException {
        String optedCategory = "tickets";
        String optedField = "_id";
        String searchValue = "unit-123";
        List<JsonObject> result = Search.performSearchOperation(optedCategory, optedField, searchValue);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test (expected = Exception.class)
    public void performSearchOperationOnInvalidCategory() throws IOException {
        String optedCategory = "user-unit-test";
        String optedField = "_id";
        String searchValue = "71";
        Search.performSearchOperation(optedCategory, optedField, searchValue);
    }

    @Test
    public void getAvailableSearchableFieldsOnValidCategoryUsers() throws Exception {
        String validCategory = "users";
        Set<String> fields = Search.getAvailableSearchableFieldsOnCategory(validCategory);
        Assert.assertNotNull(fields);
        Assert.assertFalse(fields.isEmpty());
        // randomly verify if some user fields are present
        Assert.assertTrue(fields.contains("_id"));
        Assert.assertTrue(fields.contains("name"));
        Assert.assertTrue(fields.contains("role"));
    }

    @Test
    public void getAvailableSearchableFieldsOnValidCategoryOrgs() throws Exception {
        String validCategory = "organizations";
        Set<String> fields = Search.getAvailableSearchableFieldsOnCategory(validCategory);
        Assert.assertNotNull(fields);
        Assert.assertFalse(fields.isEmpty());
        // randomly verify if some user fields are present
        Assert.assertTrue(fields.contains("_id"));
        Assert.assertTrue(fields.contains("name"));
        Assert.assertTrue(fields.contains("domain_names"));
    }

    @Test
    public void getAvailableSearchableFieldsOnValidCategoryTickets() throws Exception {
        String validCategory = "tickets";
        Set<String> fields = Search.getAvailableSearchableFieldsOnCategory(validCategory);
        Assert.assertNotNull(fields);
        Assert.assertFalse(fields.isEmpty());
        // randomly verify if some user fields are present
        Assert.assertTrue(fields.contains("_id"));
        Assert.assertTrue(fields.contains("submitter_id"));
        Assert.assertTrue(fields.contains("assignee_id"));
        Assert.assertTrue(fields.contains("subject"));
    }

    @Test(expected = Exception.class)
    public void getAvailableSearchableFieldsOnValidCategoryInvalid() throws Exception {
        String validCategory = "unit-test";
        Search.getAvailableSearchableFieldsOnCategory(validCategory);

    }
}