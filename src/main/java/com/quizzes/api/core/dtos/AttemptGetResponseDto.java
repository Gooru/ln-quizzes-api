package com.quizzes.api.core.dtos;

import java.util.UUID;

public class AttemptGetResponseDto extends StartContextEventResponseDto {
    private UUID attemptId;
    private UUID profileId;
    private EventSummaryDataDto eventSummary;

    public UUID getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(UUID attemptId) {
        this.attemptId = attemptId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public EventSummaryDataDto getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(EventSummaryDataDto eventSummary) {
        this.eventSummary = eventSummary;
    }
}
