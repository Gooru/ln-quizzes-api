package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface ContextProfileWithContextEntity extends ContextEntity {

    UUID getContextProfileId();

    void setContextProfileId(UUID contextProfileId);

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    boolean getIsComplete();

    void setIsComplete(boolean isComplete);

}
