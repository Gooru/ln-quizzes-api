package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.Resource;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository {
    List<Resource> getResourcesByCollectionId(UUID collectionId);

}
