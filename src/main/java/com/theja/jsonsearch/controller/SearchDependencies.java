package com.theja.jsonsearch.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class will hold the helper methods that build logic to retrieve the dependencies for search categories
 * Ex: User category (users.json file) will have organization_id as a field so organization is a dependency
 * Tickets entity (tickets.json file) will have users i.e., submitter_id and assignee_id as dependencies
 */
public class SearchDependencies {

    private static final Logger LOGGER = LogManager.getLogger();

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

    /**
     * This method appends related fields on the returned search results.
     * Currently we only handle related fields for tickets and users category
     * meaning,
     *
     * when a match is found on one or more ticket objects, on each of the returned ticket object we
     *  - retrieve the name of organization for this ticket
     *  - retrieve the name of user who requested this ticket
     *  - retrieve the name of user who got this ticket assigned
     * when a match is found on one or many user objects, on each of the returned user object we
     *  - retrieve the name of user organization
     *  - If any tickets created by users show the ticket subject name
     *
     *  Note: I did not implement fetching the user and ticket details for organization search as there will be
     *  many to many entries in both cases, but can be done same way as below
     */
    public static JsonObject appendRelatedFields(String currentOptedCategory, JsonObject jsonObject) throws IOException {
        if (StringUtils.equalsIgnoreCase(currentOptedCategory, TICKETS)) {
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
        LOGGER.debug(String.format("Fetching user name from %s", userIdField));
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
        LOGGER.debug("Fetching organization name from orgId");
        if(jsonObject.get(JSON_FIELD_ORG_ID) != null && StringUtils.isNotEmpty(jsonObject.get(JSON_FIELD_ORG_ID).getAsString())) {
            String id = jsonObject.get(JSON_FIELD_ORG_ID).getAsString();
            List<JsonObject> orgDetails = Search.performSearchOperation(ORGS, JSON_FIELD_ID, id);
            orgDetails.stream()
                    .findFirst()
                    .ifPresent(orgDetail -> jsonObject.addProperty(JSON_KEY_ORGANIZATION_NAME,
                            orgDetail.get(JSON_FIELD_NAME).getAsString()));
        }
        return jsonObject;
    }

    /**
     * Retrieves all the tickets submitted by the user
     * If there are multiple tickets, the json key is appended in key_i format where i is number of the ticket
     */
    private static JsonObject appendTicketDetailsSubmittedByUser(JsonObject jsonObject) throws IOException {
        LOGGER.debug("Fetching all ticket subjects submitted by selected user");
        String id = jsonObject.get(JSON_FIELD_ID).getAsString();
        List<JsonObject> ticketDetails = Search.performSearchOperation(TICKETS, TICKET_FIELD_SUBMITTER_ID, id);
        AtomicInteger counter = new AtomicInteger(0);
        ticketDetails.forEach(ticketDetail ->
                jsonObject.addProperty(JSON_KEY_TICKETS + counter.addAndGet(1),
                        ticketDetail.get(TICKET_FIELD_SUBJECT).getAsString()));
        return jsonObject;
    }
}
