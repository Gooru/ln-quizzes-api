package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextEventEntity {

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeId);

    String getEventData();

    void setEventData(String eventData);

}

