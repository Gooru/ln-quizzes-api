package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.UtilsRepository;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class UtilsRepositoryImpl implements UtilsRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public long getCurrentTimestamp() {
        return jooq.select(DSL.currentTimestamp()).fetchOneInto(Timestamp.class).getTime();
    }
}
