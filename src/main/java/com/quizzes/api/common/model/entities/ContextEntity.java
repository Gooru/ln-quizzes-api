package com.quizzes.api.common.model.entities;

import com.quizzes.api.common.model.jooq.tables.pojos.Context;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextEntity {

    UUID getId();

    void setId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getGroupId();

    void setGroupId(UUID groupId);

    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

    String getContextData();

    void setContextData(String contextData);

    boolean isActive();

    void setIsActive(boolean isActive);

}
