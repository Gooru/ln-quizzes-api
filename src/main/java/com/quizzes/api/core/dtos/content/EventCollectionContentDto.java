package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * This Dto will work for collection.play/collection.stop
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCollectionContentDto extends EventContentCommonDto {

    private ContextCollectionEventContentDto context;
    private PayloadObjectCollectionEventContentDto payLoadObject;

    @Builder
    public EventCollectionContentDto(UUID eventId, String eventName, SessionEventContentDto session,
                                     UserEventContentDto user, VersionEventContentDto version, MetricsEventContentDto metrics,
                                     long startTime, Long endTime, ContextCollectionEventContentDto context,
                                     PayloadObjectCollectionEventContentDto payLoadObject) {
        super(eventId, eventName, session, user, version, metrics, startTime, endTime);
        this.context = context;
        this.payLoadObject = payLoadObject;
    }
}
