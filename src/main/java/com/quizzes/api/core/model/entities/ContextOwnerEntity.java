package com.quizzes.api.core.model.entities;

import java.util.UUID;

public interface ContextOwnerEntity extends ContextEntity {

    UUID getOwnerProfileId();

    void setOwnerProfileId(UUID ownerProfileId);

    UUID getContextProfileId();

    void setContextProfileId(UUID contextProfileId);

}
