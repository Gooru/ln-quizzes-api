package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.quizzes.api.core.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.core.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;
import static com.quizzes.api.core.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;

@Repository
public class ContextRepositoryImpl implements ContextRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Context findById(UUID id) {
        return jooq.select()
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(id))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchOneInto(Context.class);
    }

    @Override
    public ContextEntity findCreatedContextByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return jooq.select(getContextFields())
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(contextId))
                .and(CONTEXT.PROFILE_ID.eq(profileId))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchOneInto(ContextEntity.class);
    }

    @Override
    public List<ContextEntity> findCreatedContextsByProfileId(UUID profileId) {
        return jooq.select(getContextFields())
                .from(CONTEXT)
                .where(CONTEXT.PROFILE_ID.eq(profileId))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchInto(ContextEntity.class);
    }

    @Override
    public AssignedContextEntity findAssignedContextByContextIdAndProfileId(UUID contextId, UUID profileId) {
        List<Field<?>> fields = getContextFields();
        fields.add(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID.as("current_context_profile_id"));
        return jooq.select(fields)
                .from(CONTEXT)
                .join(CONTEXT_PROFILE).on(CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .leftJoin(CURRENT_CONTEXT_PROFILE).on(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .where(CONTEXT.ID.eq(contextId))
                .and(CONTEXT.IS_ACTIVE.eq(true))
                .and(CONTEXT.IS_DELETED.eq(false))
                .limit(1)
                .fetchOneInto(AssignedContextEntity.class);
    }

    @Override
    public List<AssignedContextEntity> findAssignedContextsByProfileId(UUID profileId) {
        List<Field<?>> fields = getContextFields();
        fields.add(CURRENT_CONTEXT_PROFILE.CONTEXT_PROFILE_ID.as("current_context_profile_id"));
        return jooq.selectDistinct(CONTEXT.ID).select(fields)
                .from(CONTEXT)
                .join(CONTEXT_PROFILE).on(CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .leftJoin(CURRENT_CONTEXT_PROFILE).on(CURRENT_CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CURRENT_CONTEXT_PROFILE.PROFILE_ID.eq(profileId)))
                .where(CONTEXT.IS_ACTIVE.eq(true))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchInto(AssignedContextEntity.class);
    }


    @Override
    public Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId) {
        return null;
        // TODO Re-implement this query
        /*
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA,
                CONTEXT.CREATED_AT, CONTEXT.UPDATED_AT, GROUP_PROFILE.PROFILE_ID.as("AssigneeProfileId"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchGroups(CONTEXT.ID, ContextAssigneeEntity.class);
                */
    }

    @Override
    public Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByContextIdAndOwnerId(UUID contextId,
                                                                                           UUID ownerId) {
        return null;
        // TODO Re-implement this query
        /*
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA,
                CONTEXT.CREATED_AT, CONTEXT.UPDATED_AT, GROUP_PROFILE.PROFILE_ID.as("AssigneeProfileId"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(CONTEXT.ID.eq(contextId))
                .and(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchGroups(CONTEXT.ID, ContextAssigneeEntity.class);
        */
    }

    /**
     * @see ContextRepository#findContextOwnerByAssigneeIdAndFilters(UUID, Boolean, Long, Long)
     */
    @Override
    public List<ContextOwnerEntity> findContextOwnerByAssigneeIdAndFilters(UUID assigneeId, Boolean isActive,
                                                                           Long startDateMillis, Long dueDateMillis) {
        return null;
        // TODO Re-implement this query
        /*
        Condition condition = null;
        if (isActive != null) {
            //if the isActive parameter is present we use that value
            condition = CONTEXT.IS_ACTIVE.eq(isActive);
        }
        else {
            //if the isActive parameter is NOT present we true as default
            condition = CONTEXT.IS_ACTIVE.eq(true);
        }
        if (startDateMillis != null) {
            condition = condition
                    .and("jsonb_typeof(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'startDate') = 'number'")
                    .and("cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'startDate' as text)::bigint " +
                            ">= " + startDateMillis);
        }
        if (dueDateMillis != null) {
            condition = condition
                    .and("jsonb_typeof(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'dueDate') = 'number'")
                    .and("cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'dueDate' as text)::bigint " +
                            "<= " + dueDateMillis);
        }
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.CONTEXT_DATA, CONTEXT.CREATED_AT,
                GROUP.OWNER_PROFILE_ID, CONTEXT_PROFILE.ID.as("context_profile_id"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(GROUP.ID)
                        .and(GROUP_PROFILE.PROFILE_ID.eq(assigneeId)))
                .leftJoin(CONTEXT_PROFILE).on(CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CONTEXT_PROFILE.PROFILE_ID.eq(assigneeId)))
                .where(condition)
                .fetchInto(ContextOwnerEntity.class);
        */
    }

    @Override
    public ContextOwnerEntity findContextOwnerByContextIdAndAssigneeId(UUID contextId, UUID assigneeId) {
        return null;
        // TODO Re-implement this query
        /*
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.CONTEXT_DATA, CONTEXT.CREATED_AT,
                GROUP.OWNER_PROFILE_ID, CONTEXT_PROFILE.ID.as("context_profile_id"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(GROUP.ID)
                        .and(GROUP_PROFILE.PROFILE_ID.eq(assigneeId)))
                .leftJoin(CONTEXT_PROFILE).on(CONTEXT_PROFILE.CONTEXT_ID.eq(CONTEXT.ID)
                        .and(CONTEXT_PROFILE.PROFILE_ID.eq(assigneeId)))
                .where(CONTEXT.ID.eq(contextId))
                .and(CONTEXT.IS_ACTIVE.eq(true))
                .fetchOneInto(ContextOwnerEntity.class);
        */
    }

    @Override
    public List<ContextEntity> findMappedContexts(UUID classId, UUID collectionId, Map<String, String> contextMap) {
        Condition condition = CONTEXT.CLASS_ID.eq(classId)
                .and(CONTEXT.COLLECTION_ID.eq(collectionId))
                .and(CONTEXT.IS_ACTIVE.eq(true))
                .and(CONTEXT.IS_DELETED.eq(false));

        for (String key : contextMap.keySet()) {
            condition = condition
                    .and(String.format("CONTEXT.CONTEXT_DATA -> 'contextMap' ->> '%s' = '%s'", key,
                            contextMap.get(key)));
        }

        return jooq.select(getContextFields())
                .from(CONTEXT)
                .where(condition)
                .fetchInto(ContextEntity.class);
    }

    @Override
    public ContextEntity findByContextMapKey(String contextMapKey) {
        return jooq.select(getContextFields())
                .from(CONTEXT)
                .where(CONTEXT.CONTEXT_MAP_KEY.eq(contextMapKey))
                .fetchOneInto(ContextEntity.class);
    }

    @Override
    public Context save(final Context context) {
        if (context.getId() == null) {
            return insertContext(context);
        } else {
            return updateContext(context);
        }
    }

    private Context insertContext(final Context context) {
        return jooq.insertInto(CONTEXT)
                .set(CONTEXT.ID, UUID.randomUUID())
                .set(CONTEXT.COLLECTION_ID, context.getCollectionId())
                .set(CONTEXT.IS_COLLECTION, context.getIsCollection())
                .set(CONTEXT.PROFILE_ID, context.getProfileId())
                .set(CONTEXT.CLASS_ID, context.getClassId())
                .set(CONTEXT.CONTEXT_MAP_KEY, context.getContextMapKey())
                .set(CONTEXT.CONTEXT_DATA, context.getContextData())
                .returning()
                .fetchOne()
                .into(Context.class);
    }

    private Context updateContext(final Context context) {
        return jooq.update(CONTEXT)
                .set(CONTEXT.CONTEXT_DATA, context.getContextData())
                .where(CONTEXT.ID.eq(context.getId()))
                .returning()
                .fetchOne()
                .into(Context.class);
    }

    private List<Field<?>> getContextFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(CONTEXT.ID.as("context_id"));
        fields.add(CONTEXT.COLLECTION_ID);
        fields.add(CONTEXT.IS_COLLECTION);
        fields.add(CONTEXT.PROFILE_ID);
        fields.add(CONTEXT.CLASS_ID);
        fields.add(CONTEXT.IS_ACTIVE);
        fields.add(CONTEXT.START_DATE);
        fields.add(CONTEXT.DUE_DATE);
        fields.add(CONTEXT.CONTEXT_DATA);
        fields.add(CONTEXT.CREATED_AT);
        fields.add(CONTEXT.UPDATED_AT);
        return fields;
    }

}
