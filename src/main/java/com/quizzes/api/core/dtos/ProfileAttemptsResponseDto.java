package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class ProfileAttemptsResponseDto {

    private UUID currentResourceId;
    private UUID profileId;
    private List<PostResponseResourceDto> events;
    private boolean isComplete;
    private EventSummaryDataDto eventSummary;
    private List<TaxonomySummaryDto> taxonomySummary;

    public ProfileAttemptsResponseDto() {
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

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public EventSummaryDataDto getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(EventSummaryDataDto eventSummary) {
        this.eventSummary = eventSummary;
    }

    public List<TaxonomySummaryDto> getTaxonomySummary() {
        return taxonomySummary;
    }

    public void setTaxonomySummary(List<TaxonomySummaryDto> taxonomySummary) {
        this.taxonomySummary = taxonomySummary;
    }
}
