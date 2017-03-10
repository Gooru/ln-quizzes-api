package com.quizzes.api.core.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextEntity {

    UUID getContextId();

    void setContextId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    Boolean getIsCollection();

    void setIsCollection(Boolean isCollection);

    UUID getProfileId();

    void setProfileId(UUID profileId);

    UUID getClassId();

    void setClassId(UUID classId);

    boolean getIsActive();

    void setIsActive(boolean isActive);

    Timestamp getStartDate();

    void setStartDate(Timestamp startDate);

    Timestamp getDueDate();

    void setDueDate(Timestamp dueDate);

    String getContextData();

    void setContextData(String contextData);

    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

    Timestamp getUpdatedAt();

    void setUpdatedAt(Timestamp updateAt);

}
