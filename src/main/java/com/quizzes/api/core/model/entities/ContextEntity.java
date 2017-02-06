package com.quizzes.api.core.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextEntity {

    UUID getContextId();

    void setContextId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

    String getContextData();

    void setContextData(String contextData);

    boolean getIsActive();

    void setIsActive(boolean isActive);

}
