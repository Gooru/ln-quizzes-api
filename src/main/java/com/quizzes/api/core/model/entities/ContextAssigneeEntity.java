package com.quizzes.api.core.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeProfileId);

    Timestamp getUpdatedAt();

    void setUpdatedAt(Timestamp updatedAt);
}
