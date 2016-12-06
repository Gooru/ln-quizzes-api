package com.quizzes.api.common.dto;

import java.util.List;
import java.util.UUID;

public class ProfileEventResponseDto {

    private UUID currentResourceId;
    private UUID profileId;
    private List<PostResponseResourceDto> events;

    public ProfileEventResponseDto() {
    }

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public List<PostResponseResourceDto> getEvents() {
        return events;
    }

    public void setEvents(List<PostResponseResourceDto> events) {
        this.events = events;
    }
}
