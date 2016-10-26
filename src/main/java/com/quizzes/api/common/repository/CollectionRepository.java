package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.enums.Lms;

public interface CollectionRepository {

    Collection findByExternalIdAndLmsId(String externalId, Lms lmsId);

}