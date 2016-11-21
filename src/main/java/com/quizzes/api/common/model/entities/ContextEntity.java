package com.quizzes.api.common.model.entities;


import java.util.UUID;

public interface ContextEntity {

    UUID getId();

    void setId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getGroupId();

    void setGroupId(UUID groupId);

    String getContextData();

    void setContextData(String contextData);

}
