package com.theja.jsonsearch.controller;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SearchTest {

    @Test
    public void performSuccessfulSearchOperationOnUsersCategory() {

    }

    @Test
    public void performSuccessfulSearchOperationOnOrganizationCategory() {

    }

    @Test
    public void performSuccessfulSearchOperationOnTicketsCategory() {

    }

    @Test
    public void performUnSuccessfulSearchOperationOnUsersCategory() {

    }

    @Test
    public void performUnSuccessfulSearchOperationOnOrganizationCategory() {

    }

    @Test
    public void performUnSuccessfulSearchOperationOnTicketsCategory() {

    }

    @Test
    public void performSearchOperationOnInvalidCategory() {

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