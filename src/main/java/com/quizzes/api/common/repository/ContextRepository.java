package com.quizzes.api.common.repository;

import com.quizzes.api.common.entities.ContextAssigneeEntity;
import com.quizzes.api.common.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.tables.pojos.Context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    List<Context> findByOwnerId(UUID ownerId);

    Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId);

    List<ContextOwnerEntity> findContextOwnerByAssigneeId(UUID assigneeId);

    ContextOwnerEntity findContextOwnerByContextId(UUID contextId);

}
