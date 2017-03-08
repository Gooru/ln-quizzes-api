package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.CurrentContextProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.core.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.core.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;
import static com.quizzes.api.core.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;

@Repository
public class CurrentContextProfileImpl implements CurrentContextProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return jooq.select(CURRENT_CONTEXT_PROFILE.CONTEXT_ID, CURRENT_CONTEXT_PROFILE.PROFILE_ID,
                CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID)
                .from(CURRENT_CONTEXT_PROFILE)
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId))
                .fetchOneInto(CurrentContextProfile.class);
    }

    @Override
    public ContextProfileEntity findCurrentContextProfileByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return jooq.select(CONTEXT.ID.as("context_id"), CONTEXT.IS_COLLECTION, CONTEXT.COLLECTION_ID,
                CONTEXT_PROFILE.ID.as("context_profile_id"), CONTEXT_PROFILE.PROFILE_ID, CONTEXT.CLASS_ID,
                CONTEXT_PROFILE.CURRENT_RESOURCE_ID, CONTEXT_PROFILE.IS_COMPLETE, CONTEXT_PROFILE.CONTEXT_PROFILE_DATA,
                CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID.as("current_context_profile_id"))
                .from(CONTEXT)
                .leftJoin(CONTEXT_PROFILE).on(CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .leftJoin(CURRENT_CONTEXT_PROFILE).on(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .where(CONTEXT.ID.eq(contextId))
                .and(CONTEXT.IS_ACTIVE.eq(true))
                .and(CONTEXT.IS_DELETED.eq(false))
                .orderBy(CONTEXT_PROFILE.CREATED_AT.desc()).limit(1)
                .fetchOneInto(ContextProfileEntity.class);
    }

    @Override
    public CurrentContextProfile create(CurrentContextProfile currentContextProfile) {
        return jooq.insertInto(CURRENT_CONTEXT_PROFILE)
                .set(CURRENT_CONTEXT_PROFILE.CONTEXT_ID, currentContextProfile.getContextId())
                .set(CURRENT_CONTEXT_PROFILE.PROFILE_ID, currentContextProfile.getProfileId())
                .set(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID, currentContextProfile.getContextProfileId())
                .returning()
                .fetchOne()
                .into(CurrentContextProfile.class);
    }

    @Override
    public void delete(CurrentContextProfile currentContextProfile) {
        jooq.deleteFrom(CURRENT_CONTEXT_PROFILE)
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(currentContextProfile.getContextId()))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(currentContextProfile.getProfileId()))
                .execute();
    }

}

