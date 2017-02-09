package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface ContextProfileEntity extends AssignedContextEntity {

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    Boolean getIsComplete();

    void setIsComplete(Boolean isComplete);

}