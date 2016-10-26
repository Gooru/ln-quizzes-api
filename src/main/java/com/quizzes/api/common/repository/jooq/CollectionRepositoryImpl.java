package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.repository.CollectionRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionRepositoryImpl implements CollectionRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Collection findByExternalIdAndLmsId(String externalId, Lms lmsId) {
        return null;
    }

}
