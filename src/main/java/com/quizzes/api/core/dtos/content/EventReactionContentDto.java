package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class EventReactionContentDto extends EventContentCommonDto {

    private ContextReactionEventContentDto context;

    @Builder
    public EventReactionContentDto(UUID eventId, String eventName, SessionEventContentDto session, UserEventContentDto user, VersionEventContentDto version, MetricsEventContentDto metrics, Long startTime, Long endTime, ContextReactionEventContentDto context) {
        super(eventId, eventName, session, user, version, metrics, startTime, endTime);
        this.context = context;
    }
}
