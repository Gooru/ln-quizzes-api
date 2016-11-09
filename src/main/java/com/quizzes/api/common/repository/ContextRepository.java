package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.Context;

import java.util.UUID;

public interface ContextRepository {

    Context save(Context context);

    Context findById(UUID id);

    Context mockedFindById(UUID id);

    Context findByCollectionIdAndGroupId(UUID collectionId, UUID groupId);

    UUID findCollectionIdByContextId(UUID contextId);

}
