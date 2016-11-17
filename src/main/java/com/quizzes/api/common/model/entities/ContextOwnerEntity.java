package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextOwnerEntity {
    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getProfileId();

    void setProfileId(UUID profileId);

    String getProfileData();

    void setProfileData(String profileData);

    String getContextData();

    void setContextData(String contextData);

}
