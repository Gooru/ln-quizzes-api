package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;

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

    /**
     * Finds the list of all {@link ContextOwnerEntity} for an assignee based on four criteria.
     * 1 - assigneeId, mandatory
     * 2 - isActive flag, optional param, default is true
     * 3 - startDate, optional, default is null
     * 4 - dueDate, optional, default is null
     *
     * @param assigneeId This is mandatory
     * @param isActive if null, then the default is true, can't be used with startDate or dueDate
     * @param startDate if not null the query looks for records with startDate >= than this param, can't be used with isActive
     * @param dueDate if not null the query looks for records with dueDate <= than this param, can't be used with isActive
     * @return the list of {@link ContextAssigneeEntity} found
     */
    List<ContextOwnerEntity> findContextOwnerByAssigneeIdAndFilters(UUID assigneeId, Boolean isActive, Long startDate, Long dueDate);

    ContextOwnerEntity findContextOwnerByContextIdAndAssigneeId(UUID contextId, UUID assigneeId);

    /**
     * Finds the a combinated entity with the {@link Context}
     * and the assigned {@link com.quizzes.api.core.model.jooq.tables.pojos.Profile} ID
     * @param contextId {@link Context} ID to find
     * @return a list with the rows containing the Context and Assignee IDs
     */
    List<ContextAssigneeEntity> findContextAssigneeByContextId(UUID contextId);

    }
