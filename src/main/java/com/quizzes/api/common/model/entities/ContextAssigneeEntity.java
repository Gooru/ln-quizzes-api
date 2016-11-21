package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeProfileId();

    void setAssigneeProfileId(UUID assigneeId);

}
