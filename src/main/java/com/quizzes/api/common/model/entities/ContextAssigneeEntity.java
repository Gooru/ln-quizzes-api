package com.quizzes.api.common.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeProfileId);

    Timestamp getModifiedAt();

    void setModifiedAt(Timestamp modifiedAt);
}
