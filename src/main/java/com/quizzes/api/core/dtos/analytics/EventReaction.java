package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class EventReaction extends EventCommon {

    private ContextReaction context;

    @Builder
    public EventReaction(UUID eventId, String eventName, Session session, User user,
                         Version version, Map metrics, Long startTime,
                         Long endTime, ContextReaction context) {
        super(eventId, eventName, session, user, version, metrics, startTime, endTime);
        this.context = context;
    }
}
