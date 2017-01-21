package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    ContextOwnerEntity findContextOwnerById(UUID id);

    List<Context> findByOwnerId(UUID ownerId);

    Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId);

    Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByContextIdAndOwnerId(UUID contextId, UUID ownerId);

    List<ContextOwnerEntity> findContextOwnerByAssigneeId(UUID assigneeId, Boolean isActive, Date startDate, Date dueDate);

    ContextOwnerEntity findContextOwnerByContextIdAndAssigneeId(UUID contextId, UUID assigneeId);

    /**
     * Finds the a combinated entity with the {@link Context}
     * and the assigned {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile} ID
     * @param contextId {@link Context} ID to find
     * @return a list with the rows containing the Context and Assignee IDs
     */
    List<ContextAssigneeEntity> findContextAssigneeByContextId(UUID contextId);

    }
