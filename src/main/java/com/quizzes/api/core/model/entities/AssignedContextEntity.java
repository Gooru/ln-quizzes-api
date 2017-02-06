package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface AssignedContextEntity extends ContextEntity {

    UUID getCurrentContextProfileId();

    void setCurrentContextProfileId(UUID currentContextProfileId);

}
