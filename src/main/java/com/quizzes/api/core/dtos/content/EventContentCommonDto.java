package com.quizzes.api.core.dtos.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * This Dto will work for collection.play/stop and collection.resource.play/stop
 */
@Data
@AllArgsConstructor
public class EventContentCommonDto {
    UUID eventId;
    String eventName;
    SessionEventContentDto session;
    private UserEventContentDto user;
    private VersionEventContentDto version;
    private MetricsEventContentDto metrics;
    private long startTime;
    private long endTime;
}
