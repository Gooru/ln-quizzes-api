package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface ContextProfileEventEntity {

    UUID getContextProfileId();

    void setContextProfileId(UUID contextProfileId);

    UUID getContextId();

    void setContextId(UUID contextId);

    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getProfileId();

    void setProfileId(UUID profileId);

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    UUID getResourceId();

    void setResourceId(UUID resourceId);

    String getEventData();

    void setEventData(String eventData);

    String getEventsSummary();

    void setEventsSummary(String eventsSummary);

    String getTaxonomySummary();

    void setTaxonomySummary(String taxonomySummary);

    Long getCreatedAt();

    void setCreatedAt(Long createdAt);

    Long getUpdatedAt();

    void setUpdatedAt(Long updatedAt);

}
