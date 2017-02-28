package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextProfileEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.repositories.ContextProfileEventRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.quizzes.api.core.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.core.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;
import static com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT;
import static com.quizzes.api.core.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;

@Repository
public class ContextProfileEventRepositoryImpl implements ContextProfileEventRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId) {
        return jooq.select(CONTEXT_PROFILE_EVENT.ID, CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID,
                CONTEXT_PROFILE_EVENT.RESOURCE_ID, CONTEXT_PROFILE_EVENT.EVENT_DATA)
                .from(CONTEXT_PROFILE_EVENT)
                .where(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(contextProfileId))
                .fetchInto(ContextProfileEvent.class);
    }

    @Override
    public Map<UUID, List<AssigneeEventEntity>> findByContextIdGroupByProfileId(UUID contextId) {
        return jooq.select(CURRENT_CONTEXT_PROFILE.PROFILE_ID.as("assigneeProfileId"),
                CONTEXT_PROFILE.CURRENT_RESOURCE_ID, CONTEXT_PROFILE.IS_COMPLETE, CONTEXT_PROFILE_EVENT.EVENT_DATA,
                CONTEXT_PROFILE.EVENT_SUMMARY_DATA.as("EventsSummary"),
                CONTEXT_PROFILE.TAXONOMY_SUMARY_DATA.as("TaxonomySummary"))
                .from(CURRENT_CONTEXT_PROFILE)
                .join(CONTEXT_PROFILE).on(CONTEXT_PROFILE.ID.eq(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID))
                .leftJoin(CONTEXT_PROFILE_EVENT).on(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(CONTEXT_PROFILE.ID))
                .where(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .fetchGroups(CURRENT_CONTEXT_PROFILE.PROFILE_ID.as("assigneeProfileId"), AssigneeEventEntity.class);
    }

    @Override
    public List<AssigneeEventEntity> findByContextIdAndProfileId(UUID contextId, UUID assigneeProfileId) {
        return jooq.select(CURRENT_CONTEXT_PROFILE.PROFILE_ID.as("AssigneeProfileId"),
                CONTEXT_PROFILE.CURRENT_RESOURCE_ID, CONTEXT_PROFILE.IS_COMPLETE, CONTEXT_PROFILE_EVENT.EVENT_DATA,
                CONTEXT_PROFILE.EVENT_SUMMARY_DATA.as("EventsSummary"),
                CONTEXT_PROFILE.TAXONOMY_SUMARY_DATA.as("TaxonomySummary"))
                .from(CURRENT_CONTEXT_PROFILE)
                .join(CONTEXT_PROFILE).on(CONTEXT_PROFILE.ID.eq(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID))
                .leftJoin(CONTEXT_PROFILE_EVENT).on(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(CONTEXT_PROFILE.ID))
                .where(CONTEXT_PROFILE.CONTEXT_ID.eq(contextId))
                .and(CONTEXT_PROFILE.PROFILE_ID.eq(assigneeProfileId))
                .fetchInto(AssigneeEventEntity.class);
    }

    @Override
    public List<ContextProfileEventEntity> findByContextProfileIdAndProfileId(UUID contextProfileId, UUID profileId) {
        return jooq.select(CONTEXT_PROFILE.ID.as("ContextProfileId"), CONTEXT_PROFILE.CONTEXT_ID, CONTEXT.COLLECTION_ID,
                CONTEXT_PROFILE.PROFILE_ID, CONTEXT_PROFILE.CURRENT_RESOURCE_ID, CONTEXT_PROFILE_EVENT.RESOURCE_ID,
                CONTEXT_PROFILE_EVENT.EVENT_DATA, CONTEXT_PROFILE.EVENT_SUMMARY_DATA.as("EventsSummary"),
                CONTEXT_PROFILE.TAXONOMY_SUMARY_DATA.as("TaxonomySummary"))
                .from(CONTEXT_PROFILE)
                .join(CONTEXT).on(CONTEXT.ID.eq(CONTEXT_PROFILE.CONTEXT_ID))
                .leftJoin(CONTEXT_PROFILE_EVENT).on(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(CONTEXT_PROFILE.ID))
                .where(CONTEXT_PROFILE.ID.eq(contextProfileId))
                .and(CONTEXT.PROFILE_ID.eq(profileId).or(CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchInto(ContextProfileEventEntity.class);
    }

    @Override
    public ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {
        return jooq.select(CONTEXT_PROFILE_EVENT.ID, CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID,
                CONTEXT_PROFILE_EVENT.RESOURCE_ID, CONTEXT_PROFILE_EVENT.EVENT_DATA)
                .from(CONTEXT_PROFILE_EVENT)
                .where(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(contextProfileId))
                .and(CONTEXT_PROFILE_EVENT.RESOURCE_ID.eq(resourceId))
                .fetchOneInto(ContextProfileEvent.class);
    }

    @Override
    public void deleteByContextProfileId(UUID contextProfileId) {
        jooq.deleteFrom(CONTEXT_PROFILE_EVENT)
                .where(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID.eq(contextProfileId))
                .execute();
    }

    @Override
    public ContextProfileEvent save(final ContextProfileEvent contextProfileEvent) {
        if (contextProfileEvent.getId() == null) {
            return insertContextProfileEvent(contextProfileEvent);
        } else {
            return updateContextProfileEvent(contextProfileEvent);
        }
    }

    private ContextProfileEvent insertContextProfileEvent(final ContextProfileEvent contextProfileEvent) {
        return jooq.insertInto(CONTEXT_PROFILE_EVENT)
                .set(CONTEXT_PROFILE_EVENT.ID, UUID.randomUUID())
                .set(CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID, contextProfileEvent.getContextProfileId())
                .set(CONTEXT_PROFILE_EVENT.RESOURCE_ID, contextProfileEvent.getResourceId())
                .set(CONTEXT_PROFILE_EVENT.EVENT_DATA, contextProfileEvent.getEventData())
                .returning()
                .fetchOne()
                .into(ContextProfileEvent.class);
    }

    private ContextProfileEvent updateContextProfileEvent(final ContextProfileEvent contextProfileEvent) {
        return jooq.update(CONTEXT_PROFILE_EVENT)
                .set(CONTEXT_PROFILE_EVENT.EVENT_DATA, contextProfileEvent.getEventData())
                .where(CONTEXT_PROFILE_EVENT.ID.eq(contextProfileEvent.getId()))
                .returning()
                .fetchOne()
                .into(ContextProfileEvent.class);
    }
}
