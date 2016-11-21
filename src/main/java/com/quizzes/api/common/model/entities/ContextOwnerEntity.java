package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextOwnerEntity extends ContextEntity {

    UUID getOwnerProfileId();

    void setOwnerProfileId(UUID ownerProfileId);

}
