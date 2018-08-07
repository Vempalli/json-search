package com.theja.jsonsearch.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class will hold the helper methods that build logic to retrieve the dependencies for search categories
 * Ex: User category (users.json file) will have organization_id as a field so organization is a dependency
 * Tickets entity (tickets.json file) will have users i.e., submitter_id and assignee_id as dependencies
 */
public class SearchCategoryDependencies {

    private static final String USERS = "users";
    private static final String TICKETS = "tickets";
    private static final String ORGS = "organizations";
    private static final String JSON_FIELD_ORG_ID = "organization_id";
    private static final String TICKET_FIELD_SUBMITTER_ID = "submitter_id";
    private static final String TICKET_FIELD_ASSIGNEE_ID = "assignee_id";
    private static final String JSON_FIELD_ID = "_id";
    private static final String JSON_FIELD_NAME = "name";
    private static final String JSON_KEY_ASSIGNEE_NAME = "assignee_name";
    private static final String JSON_KEY_SUBMITTER_NAME = "submitter_name";
    private static final String JSON_KEY_ORGANIZATION_NAME = "organization_name";
    private static final String JSON_KEY_TICKETS = "tickets_";
    private static final String TICKET_FIELD_SUBJECT = "subject";

    public static JsonObject appendRelatedFields(String currentOptedCategory, JsonObject jsonObject) throws IOException {
        if (StringUtils.equalsIgnoreCase(currentOptedCategory, ORGS)) {
            // TODO
        }
        else if (StringUtils.equalsIgnoreCase(currentOptedCategory, TICKETS)) {
            // include users and org details
            jsonObject = appendOrgDetails(jsonObject);
            jsonObject = appendSingleUserDetails(jsonObject, TICKET_FIELD_ASSIGNEE_ID);
            jsonObject = appendSingleUserDetails(jsonObject, TICKET_FIELD_SUBMITTER_ID);
        }
        else if (StringUtils.equalsIgnoreCase(currentOptedCategory, USERS)) {
            // include tickets by this user and org name
            jsonObject = appendOrgDetails(jsonObject);
            jsonObject = appendTicketDetailsSubmittedByUser(jsonObject);
        }
        return jsonObject;
    }

    /**
     * Retrieve the user name based on user id. User id can be either submitter or assignee
     * Note: Assumption is that user id is not same across organizations meaning, user with given user id exists only
     * in one organization
     */
    private static JsonObject appendSingleUserDetails(JsonObject jsonObject, String userIdField) throws IOException {
        JsonElement userId = jsonObject.get(userIdField);
        String fieldName = userIdField.equals(TICKET_FIELD_ASSIGNEE_ID) ? JSON_KEY_ASSIGNEE_NAME : JSON_KEY_SUBMITTER_NAME;
        if(userId != null && StringUtils.isNotEmpty(userId.getAsString())) {
            List<JsonObject> orgDetails = Search.performSearchOperation(USERS, JSON_FIELD_ID, userId.getAsString());
            orgDetails.stream()
                    .findFirst()
                    .ifPresent(orgDetail ->
                            jsonObject.addProperty(fieldName, orgDetail.get(JSON_FIELD_NAME).getAsString()));
        }
        return jsonObject;
    }

    /**
     * Retrieves the name of organization from it's ID
     * Note: Assumption is that ticket id or user id can only belong on 1 organization
     * Because of the assumption above we just retrieve the first result
     */
    private static JsonObject appendOrgDetails(JsonObject jsonObject) throws IOException {
        if(jsonObject.get(JSON_FIELD_ORG_ID) != null && StringUtils.isNotEmpty(jsonObject.get(JSON_FIELD_ORG_ID).getAsString())) {
            String id = jsonObject.get(JSON_FIELD_ORG_ID).getAsString();
            List<JsonObject> orgDetails = Search.performSearchOperation(ORGS, JSON_FIELD_ID, id);
            orgDetails.stream()
                    .findFirst()
                    .ifPresent(orgDetail ->
                            jsonObject.addProperty(JSON_KEY_ORGANIZATION_NAME, orgDetail.get(JSON_FIELD_NAME).getAsString()));
        }
        return jsonObject;
    }

    /**
     * Retrieves all the tickets submitted by the user
     * If there are multiple tickets, the json key is appended in key_i format where i is number of the ticket
     */
    private static JsonObject appendTicketDetailsSubmittedByUser(JsonObject jsonObject) throws IOException {
        String id = jsonObject.get(JSON_FIELD_ID).getAsString();
        List<JsonObject> ticketDetails = Search.performSearchOperation(TICKETS, TICKET_FIELD_SUBMITTER_ID, id);
        AtomicInteger counter = new AtomicInteger(0);
        ticketDetails.forEach(ticketDetail ->
                jsonObject.addProperty(JSON_KEY_TICKETS + counter.addAndGet(1),
                        ticketDetail.get(TICKET_FIELD_SUBJECT).getAsString()));
        return jsonObject;
    }
}
