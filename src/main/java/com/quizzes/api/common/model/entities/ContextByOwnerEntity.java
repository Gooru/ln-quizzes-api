package com.quizzes.api.common.model.entities;

import com.quizzes.api.common.model.tables.pojos.Context;

import java.util.UUID;

public class ContextByOwnerEntity extends Context {

    private UUID assigneeId;

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }
}
