package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.AssignedContextEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.tables.pojos.Context;

import java.util.List;
import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    List<Context> findByOwnerId(UUID profileId);

    Context findByCollectionIdAndGroupId(UUID collectionId, UUID groupId);

    UUID findCollectionIdByContextId(UUID contextId);

    List<AssignedContextEntity> findAssignedContextsByProfileId(UUID profileId);

    ContextOwnerEntity findContextAndOwnerByContextId(UUID contextId);

}
