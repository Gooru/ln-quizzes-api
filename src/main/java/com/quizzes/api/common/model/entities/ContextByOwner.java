package com.quizzes.api.common.model.entities;

import com.quizzes.api.common.model.tables.pojos.Context;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class ContextByOwner extends Context implements java.io.Serializable{

    private UUID assigneeId;

    public ContextByOwner() {}

    public ContextByOwner(UUID id, UUID collectionId, UUID groupId, String contextData, Timestamp createdAt, UUID assigneeId) {
        super(id, collectionId, groupId, contextData, createdAt);
        this.assigneeId = assigneeId;
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }
}
