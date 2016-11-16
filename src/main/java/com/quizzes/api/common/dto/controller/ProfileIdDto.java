package com.quizzes.api.common.dto.controller;

import java.util.UUID;

/**
 * The request body will be converted into this object.
 */
public class ProfileIdDto {
    private UUID profileId;

    public ProfileIdDto() {
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }
}
