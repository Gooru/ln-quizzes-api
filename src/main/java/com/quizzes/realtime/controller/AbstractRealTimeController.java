package com.quizzes.realtime.controller;

public abstract class AbstractRealTimeController {

    /**
     * Builds a collection unique id using the class and collection ids
     * @param classId the class id
     * @param collectionId the collection id
     * @return a composed id
     */
    protected String buildCollectionUniqueId(String classId, String collectionId) {
        return classId + "_" + collectionId;
    }

}
