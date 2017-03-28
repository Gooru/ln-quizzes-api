package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

/**
 * This Dto will work for collection.play/collection.stop
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCollection extends EventCommon {

    private ContextCollection context;
    private PayloadObjectCollection payLoadObject;

    @Builder
    public EventCollection(UUID eventId, String eventName, Session session,
                           User user, Version version, Map metrics,
                           long startTime, Long endTime, ContextCollection context,
                           PayloadObjectCollection payLoadObject) {
        super(eventId, eventName, session, user, version, metrics, startTime, endTime);
        this.context = context;
        this.payLoadObject = payLoadObject;
    }
}
