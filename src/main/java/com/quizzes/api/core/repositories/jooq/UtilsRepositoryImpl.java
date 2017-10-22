package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.repositories.UtilsRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class UtilsRepositoryImpl implements UtilsRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public long getCurrentTimestamp() {
        return jooq.select(DSL.currentTimestamp()).fetchOneInto(Timestamp.class).getTime();
    }
}
