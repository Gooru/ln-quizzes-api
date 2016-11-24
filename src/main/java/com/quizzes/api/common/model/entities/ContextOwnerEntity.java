package com.quizzes.api.common.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextOwnerEntity extends ContextEntity {

    UUID getOwnerProfileId();

    void setOwnerProfileId(UUID ownerProfileId);

    int getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

}
