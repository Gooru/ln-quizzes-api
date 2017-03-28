package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.CurrentContextProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.core.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.core.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;
import static com.quizzes.api.core.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;

@Repository
public class CurrentContextProfileRepositoryImpl implements CurrentContextProfileRepository {

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
        return jooq.select(CURRENT_CONTEXT_PROFILE.CONTEXT_ID, CURRENT_CONTEXT_PROFILE.PROFILE_ID,
                CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID, CONTEXT.COLLECTION_ID, CONTEXT.IS_COLLECTION,
                CONTEXT.CLASS_ID, CONTEXT_PROFILE.IS_COMPLETE, CONTEXT_PROFILE.CURRENT_RESOURCE_ID)
                .from(CURRENT_CONTEXT_PROFILE)
                .join(CONTEXT).on(CONTEXT.ID.eq(CURRENT_CONTEXT_PROFILE.CONTEXT_ID)
                        .and(CONTEXT.IS_ACTIVE.eq(true))
                        .and(CONTEXT.IS_DELETED.eq(false)))
                .join(CONTEXT_PROFILE).on(CONTEXT_PROFILE.ID.eq(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID))
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId))
                .limit(1)
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

