package com.quizzes.api.common.dto.controller;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class is used to set the specific response for endpoints with this format.
 * This object will be converted in json format
 */
public class EventDTO {

    private String contextId;
    private String currentStatus;
    private ArrayList<Map<String, String>> collectionStatus;

    public EventDTO() {
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ArrayList<Map<String, String>> getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(ArrayList<Map<String, String>> collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

}
