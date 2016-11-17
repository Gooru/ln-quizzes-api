package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.ContextByOwnerEntity;
import com.quizzes.api.common.model.entities.AssignedContextEntity;
import com.quizzes.api.common.model.tables.pojos.Context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    Context mockedFindById(UUID id);

    List<Context> findByOwnerId(UUID profileId);

    Map<UUID, List<ContextByOwnerEntity>> findContextByOwnerId(UUID profileId);

    Context findByCollectionIdAndGroupId(UUID collectionId, UUID groupId);

    UUID findCollectionIdByContextId(UUID contextId);

    List<AssignedContextEntity> findAssignedContextsByProfileId(UUID profileId);

}
