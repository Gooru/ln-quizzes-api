package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.Resource;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository {

    Resource save(Resource resource);

    List<Resource> findByCollectionId(UUID collectionId);

    Resource findFirstBySequenceByContextId(UUID contextId);

}
