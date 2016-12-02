package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;

import java.util.UUID;

public interface CollectionRepository {

    Collection save(Collection collection);

    Collection findById(UUID id);

    Collection findByExternalId(String externalId);

}