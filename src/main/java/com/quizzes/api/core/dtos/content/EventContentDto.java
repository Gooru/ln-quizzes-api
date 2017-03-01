package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * This Dto will work for collection.play, collection.stop
 */
@Data
@Builder
public class EventContentDto {
    private UUID eventId;
    private String eventName;
    private SessionEventContentDto session;
    private UserEventContentDto user;
    private ContextEventContentDto context;
    private VersionEventContentDto version;
    private MetricsEventContentDto metrics;
    private PayloadObjectEventContentDto payLoadObject;
    private long startTime;
    private long endTime;
}
