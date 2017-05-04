package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

/**
 * This Dto will work for collection.resource.play/collection.stop
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EventResource extends EventCommon {

    private ContextResource context;
    private PayloadObjectResource payLoadObject;

    @Builder
    public EventResource(UUID eventId, String eventName, Session session, User user, Version version, Map metrics,
                         String timezone, Long startTime, Long endTime, ContextResource context,
                         PayloadObjectResource payLoadObject) {
        super(eventId, eventName, session, user, version, metrics, timezone, startTime, endTime);
        this.context = context;
        this.payLoadObject = payLoadObject;
    }

}
