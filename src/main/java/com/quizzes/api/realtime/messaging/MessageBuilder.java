package com.quizzes.api.realtime.messaging;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.stereotype.Component;


@Component
public class MessageBuilder {

    private JsonParser jsonParser = new JsonSimpleJsonParser();

    /**
     * Creates a generic Event Message
     * @param collectionUniqueId collection unique id
     * @param userId user id
     * @param event event message body
     */
    public String buildEventMessage(String collectionUniqueId, String userId, String event) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("{")
            .append("\"collectionUniqueId\":\"").append(collectionUniqueId).append("\",")
            .append("\"userId\":\"").append(userId).append("\",")
            .append("\"event\":").append(event)
            .append("}");
        return messageBuilder.toString();
    }

    public String buildResetCollectionEventMessage(String collectionUniqueId, String userId) {
        String event = "{ \"isNewAttempt\": true }";
        return this.buildEventMessage(collectionUniqueId, userId, event);
    }

    public String buildCompleteCollectionEventMessage(String collectionUniqueId, String userId) {
        String event = "{ \"isCompleteAttempt\": true }";
        return this.buildEventMessage(collectionUniqueId, userId, event);
    }

    public String extractCollectionUniqueIdFromMessage(String message) {
        return (String) jsonParser.parseMap(message).get("collectionUniqueId");
    }

}
