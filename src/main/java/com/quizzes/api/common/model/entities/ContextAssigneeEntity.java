package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface ContextAssigneeEntity extends ContextEntity {

    UUID getAssigneeId();

    void setAssigneeId(UUID assigneeId);

}
