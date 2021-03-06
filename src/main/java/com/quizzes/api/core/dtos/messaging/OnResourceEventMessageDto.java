package com.quizzes.api.core.dtos.messaging;

import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;

import java.util.UUID;

public class OnResourceEventMessageDto {

    private UUID currentResourceId;
    private PostRequestResourceDto previousResource;
    private EventSummaryDataDto eventSummary;

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

    public PostRequestResourceDto getPreviousResource() {
        return previousResource;
    }

    public void setPreviousResource(PostRequestResourceDto previousResource) {
        this.previousResource = previousResource;
    }

    public EventSummaryDataDto getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(EventSummaryDataDto eventSummary) {
        this.eventSummary = eventSummary;
    }

}
