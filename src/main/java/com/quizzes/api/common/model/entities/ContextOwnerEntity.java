package com.quizzes.api.common.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextOwnerEntity extends ContextEntity {

    UUID getOwnerProfileId();

    void setOwnerProfileId(UUID ownerProfileId);

    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

}
