package com.theja.jsonsearch.controller;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SearchDependenciesTest {

    @Test
    public void appendRelatedFieldsOnUserSearch() throws IOException {
        JsonObject jsonObjectWithDependencies =
                SearchDependencies.appendRelatedFields("users", buildDummyUserObject());
        Assert.assertTrue(jsonObjectWithDependencies.has("organization_name"));
        Assert.assertTrue(jsonObjectWithDependencies.has("tickets_1"));
        Assert.assertNotNull(jsonObjectWithDependencies.get("tickets_1"));
        Assert.assertNotNull(jsonObjectWithDependencies.get("organization_name"));
    }

    @Test
    public void appendRelatedFieldsOnTicketSearch() throws IOException {
        JsonObject jsonObjectWithDependencies =
                SearchDependencies.appendRelatedFields("tickets", buildDummyTicketObject());
        Assert.assertTrue(jsonObjectWithDependencies.has("submitter_name"));
        Assert.assertTrue(jsonObjectWithDependencies.has("assignee_name"));
        Assert.assertTrue(jsonObjectWithDependencies.has("organization_name"));
        Assert.assertNotNull(jsonObjectWithDependencies.get("submitter_name"));
        Assert.assertNotNull(jsonObjectWithDependencies.get("assignee_name"));
        Assert.assertNotNull(jsonObjectWithDependencies.get("organization_name"));
    }

    @Test
    public void appendNoRelatedFieldsOnInvalidSearch() throws IOException {
        JsonObject beforeProcessing = buildDummyTicketObject();
        JsonObject afterProcessing =
                SearchDependencies.appendRelatedFields("random", buildDummyTicketObject());
        // make sure no dependency fields are added
        Assert.assertTrue(beforeProcessing.equals(afterProcessing));
    }

    private JsonObject buildDummyUserObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_id", 71);
        jsonObject.addProperty("url", "http://initech.zendesk.com/api/v2/users/71.json");
        jsonObject.addProperty("name", "Prince Hinton");
        jsonObject.addProperty("organization_id", 121);
        return jsonObject;
    }

    private JsonObject buildDummyTicketObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_id", "1a227508-9f39-427c-8f57-1b72f3fab87c");
        jsonObject.addProperty("submitter_id", 71);
        jsonObject.addProperty("assignee_id", 38);
        jsonObject.addProperty("organization_id", 112);
        return jsonObject;
    }
}