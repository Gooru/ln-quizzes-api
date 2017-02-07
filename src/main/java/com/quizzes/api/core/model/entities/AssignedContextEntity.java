package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface AssignedContextEntity extends ContextEntity {

    UUID getContextProfileId();

    void setContextProfileId(UUID contextProfileId);

    UUID getCurrentContextProfileId();

    void setCurrentContextProfileId(UUID currentContextProfileId);

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    Boolean getIsComplete();

    void setIsComplete(Boolean isComplete);

}
