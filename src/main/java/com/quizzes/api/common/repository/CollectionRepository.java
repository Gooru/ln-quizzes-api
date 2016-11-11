package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;

import java.util.UUID;

public interface CollectionRepository {

    Collection save(Collection collection);

    Collection findById(UUID id);

    Collection findByExternalIdAndLmsId(String externalId, Lms lmsId);

}