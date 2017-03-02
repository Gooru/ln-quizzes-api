package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class AttemptGetResponseDto extends StartContextEventResponseDto {
    private UUID attemptId;
    private UUID profileId;
    private EventSummaryDataDto eventSummary;
    private List<TaxonomySummaryDto> taxonomySummary;

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

    public List<TaxonomySummaryDto> getTaxonomySummary() {
        return taxonomySummary;
    }

    public void setTaxonomySummary(List<TaxonomySummaryDto> taxonomySummary) {
        this.taxonomySummary = taxonomySummary;
    }
}
