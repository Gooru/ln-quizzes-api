package com.quizzes.api.core.services;

import com.quizzes.api.core.dtos.ResourceEventDto;
import com.quizzes.api.core.services.content.AnalyticsContentService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResourceEventService {

    @Autowired
    private AnalyticsContentService analyticsContentService;

    @Autowired
    private QuizzesUtils quizzesUtils;

    public void processFinishResourceEvent(ResourceEventDto resourceEvent, UUID profileId, String token) {
        UUID eventId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        Long endTime = quizzesUtils.getCurrentTimestamp();
        Long startTime = endTime - resourceEvent.getResourceEventData().getTimeSpent();
        analyticsContentService.singleResourceEventStart(eventId, resourceEvent.getResourceEventData().getResourceId(),
                profileId, sessionId, startTime, resourceEvent.getEventContext(), token);
        analyticsContentService.singleResourceEventReaction(UUID.randomUUID(),
                resourceEvent.getResourceEventData().getResourceId(), eventId, profileId, sessionId,
                resourceEvent.getResourceEventData().getReaction(), startTime, resourceEvent.getEventContext(), token);
        analyticsContentService.singleResourceEventStop(eventId, resourceEvent.getResourceEventData().getResourceId(),
                profileId, sessionId, startTime, endTime, resourceEvent.getEventContext(), token);
    }

}

