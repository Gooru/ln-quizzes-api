package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextOwnerEntity {
    UUID getContextId();

    void setContextId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getOwnerId();

    void setOwnerId(UUID profileId);

    String getContextData();

    void setContextData(String contextData);

}
