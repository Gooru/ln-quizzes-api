package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    Context findByIdAndOwnerId(UUID contextId, UUID ownerId);

    List<Context> findByOwnerId(UUID ownerId);

    Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId);

    Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByContextIdAndOwnerId(UUID contextId, UUID ownerId);

    List<ContextOwnerEntity> findContextOwnerByAssigneeId(UUID assigneeId);

    ContextOwnerEntity findContextOwnerByContextIdAndAssigneeId(UUID contextId, UUID assigneeId);

    Context findByIdAndAssigneeId(UUID contextId, UUID assigneeId);

    }
