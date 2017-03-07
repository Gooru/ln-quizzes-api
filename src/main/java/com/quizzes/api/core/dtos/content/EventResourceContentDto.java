package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * This Dto will work for collection.resource.play/collection.stop
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EventResourceContentDto extends EventContentCommonDto {

    private ContextResourceEventContentDto context;
    private PayloadObjectResourceEventContentDto payLoadObject;

    @Builder
    public EventResourceContentDto(UUID eventId, String eventName, SessionEventContentDto session, UserEventContentDto user, VersionEventContentDto version, MetricsEventContentDto metrics, long startTime, long endTime, ContextResourceEventContentDto context, PayloadObjectResourceEventContentDto payLoadObject) {
        super(eventId, eventName, session, user, version, metrics, startTime, endTime);
        this.context = context;
        this.payLoadObject = payLoadObject;
    }
}
