package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface AssigneeEventEntity {

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeProfileId);

    String getEventData();

    void setEventData(String eventData);

    String getEventsSummary();

    void setEventsSummary(String eventsSummary);

}

