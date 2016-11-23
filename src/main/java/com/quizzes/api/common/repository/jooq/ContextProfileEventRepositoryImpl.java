package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.repository.ContextProfileEventRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.quizzes.api.common.model.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT;

@Repository
public class ContextProfileEventRepositoryImpl implements ContextProfileEventRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public List<ContextProfileEvent> findEventsByContextProfileId(UUID contextProfileId) {
        return jooq.select(CONTEXT_PROFILE_EVENT.RESOURCE_ID, CONTEXT_PROFILE_EVENT.EVENT_DATA)
                .from(CONTEXT_PROFILE_EVENT)
                .where(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(contextProfileId))
                .fetchInto(ContextProfileEvent.class);
    }
}
