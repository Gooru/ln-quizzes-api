package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * This Dto will work for collection.play, collection.stop
 */
@Data
@Builder
public class EventDto {
    private UUID eventId;
    private String eventName;
    private SessionEventDto session;
    private UserEventDto user;
    private ContextEventDto context;
    private VersionEventDto version;
    private MetricsEventDto metrics;
    private PayloadObjectEventDto payLoadObject;
    private long startTime;
    private long endTime;
}
