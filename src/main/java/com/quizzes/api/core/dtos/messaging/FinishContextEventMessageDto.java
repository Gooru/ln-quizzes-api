package com.quizzes.api.core.dtos.messaging;

import com.quizzes.api.core.dtos.EventSummaryDataDto;

public class FinishContextEventMessageDto {

    private EventSummaryDataDto eventSummary;

    public EventSummaryDataDto getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(EventSummaryDataDto eventSummary) {
        this.eventSummary = eventSummary;
    }

}
