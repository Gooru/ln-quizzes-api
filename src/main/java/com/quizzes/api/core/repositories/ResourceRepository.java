package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.Resource;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository {

    Resource save(Resource resource);

    Resource findById(UUID resourceId);

    List<Resource> findByCollectionId(UUID collectionId);

    Resource findFirstByContextIdOrderBySequence(UUID contextId);

}
