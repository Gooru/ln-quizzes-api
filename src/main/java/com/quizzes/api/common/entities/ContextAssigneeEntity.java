package com.quizzes.api.common.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeId);

    Timestamp getModifiedAt();

    void setModifiedAt(Timestamp modifiedAt);
}
