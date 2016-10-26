package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.Collection;

public interface CollectionRepository {

    Collection findByExternalId(String id);

    Collection save(Collection id);

}