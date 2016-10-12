package com.quizzes.api.common.dto.controller;

import java.util.UUID;

/**
 * The request body will be converted into this object.
 */
public class ProfileIdDTO {
    private UUID profileId;

    public ProfileIdDTO() {
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }
}
