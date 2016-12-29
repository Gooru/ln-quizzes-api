package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.repository.ContextProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;
import static org.jooq.impl.DSL.count;

@Repository
public class ContextProfileRepositoryImpl implements ContextProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public ContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return jooq.select(CONTEXT_PROFILE.ID, CONTEXT_PROFILE.CONTEXT_ID, CONTEXT_PROFILE.CURRENT_RESOURCE_ID,
                CONTEXT_PROFILE.IS_COMPLETE, CONTEXT_PROFILE.PROFILE_ID)
                .from(CONTEXT_PROFILE)
                .where(CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CONTEXT_PROFILE.PROFILE_ID.eq(profileId))
                .fetchOneInto(ContextProfile.class);
    }

    @Override
    public ContextProfile save(final ContextProfile contextProfile) {
        if (contextProfile.getId() == null) {
            return insertContextProfile(contextProfile);
        } else {
            return updateContextProfile(contextProfile);
        }
    }

    @Override
    public boolean isContextStarted(UUID contextId, UUID profileId) {
        return jooq.select(count(CONTEXT_PROFILE.ID))
                .from(CONTEXT_PROFILE)
                .where(CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CONTEXT_PROFILE.PROFILE_ID.eq(profileId))
                .fetchOneInto(int.class) > 0;
    }

    private ContextProfile insertContextProfile(ContextProfile contextProfile) {
        return jooq.insertInto(CONTEXT_PROFILE)
                .set(CONTEXT_PROFILE.ID, UUID.randomUUID())
                .set(CONTEXT_PROFILE.CONTEXT_ID, contextProfile.getContextId())
                .set(CONTEXT_PROFILE.PROFILE_ID, contextProfile.getProfileId())
                .set(CONTEXT_PROFILE.CURRENT_RESOURCE_ID, contextProfile.getCurrentResourceId())
                .returning()
                .fetchOne()
                .into(ContextProfile.class);
    }

    private ContextProfile updateContextProfile(ContextProfile contextProfile) {
        return jooq.update(CONTEXT_PROFILE)
                .set(CONTEXT_PROFILE.IS_COMPLETE, contextProfile.getIsComplete())
                .set(CONTEXT_PROFILE.CURRENT_RESOURCE_ID, contextProfile.getCurrentResourceId())
                .where(CONTEXT_PROFILE.ID.eq(contextProfile.getId()))
                .returning()
                .fetchOne()
                .into(ContextProfile.class);
    }


    @Override
    public List<UUID> findContextProfileIdsByContextId(UUID contextId) {
        List<UUID> ids = new ArrayList<>();
        ids.add(UUID.randomUUID());
        ids.add(UUID.randomUUID());
        return ids;
    }

    @Override
    public void delete(UUID id) {
        //TODO: Implement functionality
    }

}
