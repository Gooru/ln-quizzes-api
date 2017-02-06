package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface ContextProfileWithContextEntity extends ContextEntity {

    UUID getProfileId();

    void setProfileId(UUID profileId);

    UUID getCurrentResourceId();

    void setCurrentResourceId(UUID currentResourceId);

    UUID getContextProfileId();

    void setContextProfileId(UUID contextProfileId);

    boolean getIsComplete();

    void setIsComplete(boolean isComplete);

}
