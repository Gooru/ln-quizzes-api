package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.CurrentContextProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.common.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;
import static com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE;

@Repository
public class CurrentContextProfileImpl implements CurrentContextProfileRepository {

    @Autowired
    private DSLContext jooq;


    @Override
    public CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return jooq.select()
                .from(CURRENT_CONTEXT_PROFILE)
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId))
                .fetchOneInto(CurrentContextProfile.class);
    }

    @Override
    public CurrentContextProfile save(CurrentContextProfile currentContextProfile) {
        return jooq.insertInto(CURRENT_CONTEXT_PROFILE)
                .set(CURRENT_CONTEXT_PROFILE.CONTEXT_ID, currentContextProfile.getContextId())
                .set(CURRENT_CONTEXT_PROFILE.PROFILE_ID, currentContextProfile.getProfileId())
                .set(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID, currentContextProfile.getContextProfileId())
                .returning()
                .fetchOne()
                .into(CurrentContextProfile.class);
    }

    @Override
    public CurrentContextProfile finish(CurrentContextProfile currentContextProfile) {
        return jooq.update(CURRENT_CONTEXT_PROFILE)
                .set(CURRENT_CONTEXT_PROFILE.IS_COMPLETE, true)
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(currentContextProfile.getContextId()))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(currentContextProfile.getProfileId()))
                .returning()
                .fetchOne()
                .into(CurrentContextProfile.class);
    }

    @Override
    public CurrentContextProfile startAttempt(CurrentContextProfile currentContextProfile) {
        return jooq.update(CURRENT_CONTEXT_PROFILE)
                .set(CURRENT_CONTEXT_PROFILE.IS_COMPLETE, false)
                .set(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID, currentContextProfile.getContextProfileId())
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(currentContextProfile.getContextId()))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(currentContextProfile.getProfileId()))
                .returning()
                .fetchOne()
                .into(CurrentContextProfile.class);
    }
}

