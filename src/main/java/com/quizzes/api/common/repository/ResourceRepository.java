package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.Resource;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository {

    Resource save(Resource resource);

    Resource findById(UUID resourceId);

    List<Resource> findByCollectionId(UUID collectionId);

    Resource findFirstBySequenceByContextId(UUID contextId);

}
