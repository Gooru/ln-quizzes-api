package com.quizzes.api.common.model.entities;

import java.util.UUID;
import java.sql.Timestamp;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeId);

    Timestamp getCreatedDate();

    void setCreatedDate(Timestamp createdDate);

    Timestamp getModifiedDate();

    void setModifiedDate(Timestamp modifiedDate);
}
