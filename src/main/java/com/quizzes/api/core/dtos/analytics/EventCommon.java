package com.quizzes.api.core.dtos.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * This Dto will work for collection.play/stop and collection.resource.play/stop
 */
@Data
@AllArgsConstructor
public class EventCommon {

    UUID eventId;
    String eventName;
    Session session;
    private User user;
    private Version version;
    private Map metrics;
    private String timezone;
    private Long startTime;
    private Long endTime;

}
