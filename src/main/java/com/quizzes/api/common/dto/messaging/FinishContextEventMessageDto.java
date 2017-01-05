package com.quizzes.api.common.dto.messaging;

import com.quizzes.api.common.dto.EventSummaryDataDto;

public class FinishContextEventMessageDto {

    private EventSummaryDataDto eventSummary;

    public EventSummaryDataDto getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(EventSummaryDataDto eventSummary) {
        this.eventSummary = eventSummary;
    }

}
