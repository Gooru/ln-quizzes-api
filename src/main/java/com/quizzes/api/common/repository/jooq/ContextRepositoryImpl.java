package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.ContextRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Context.CONTEXT;
import static com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE;
import static com.quizzes.api.common.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE;
import static com.quizzes.api.common.model.jooq.tables.Group.GROUP;
import static com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE;

@Repository
public class ContextRepositoryImpl implements ContextRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Context save(final Context context) {
        if (context.getId() == null) {
            return insertContext(context);
        } else {
            return updateContext(context);
        }
    }

    @Override
    public Context findById(UUID id) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA, CONTEXT.IS_ACTIVE)
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(id))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchOneInto(Context.class);
    }

    @Override
    public ContextOwnerEntity findContextOwnerById(UUID id) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA, CONTEXT.IS_ACTIVE,
                GROUP.OWNER_PROFILE_ID)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .where(CONTEXT.ID.eq(id))
                .and(CONTEXT.IS_DELETED.eq(false))
                .fetchOneInto(ContextOwnerEntity.class);
    }

    @Override
    public List<Context> findByOwnerId(UUID ownerId) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA, CONTEXT.IS_ACTIVE)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchInto(Context.class);
    }

    @Override
    public Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId){
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA,
                CONTEXT.CREATED_AT, CONTEXT.UPDATED_AT, GROUP_PROFILE.PROFILE_ID.as("AssigneeProfileId"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchGroups(CONTEXT.ID, ContextAssigneeEntity.class);
    }

    @Override
    public Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByContextIdAndOwnerId(UUID contextId, UUID ownerId){
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA,
                CONTEXT.CREATED_AT, CONTEXT.UPDATED_AT, GROUP_PROFILE.PROFILE_ID.as("AssigneeProfileId"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(CONTEXT.ID.eq(contextId))
                .and(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchGroups(CONTEXT.ID, ContextAssigneeEntity.class);
    }

    @Override
    public List<ContextOwnerEntity> findContextOwnerByAssigneeId(UUID assigneeId, Boolean isActive, Date startDate, Date dueDate) {
        Condition condition = CONTEXT.IS_ACTIVE.eq(true);
        if (isActive != null) {
            condition = condition.and(CONTEXT.IS_ACTIVE.eq(isActive));
        }
        if (startDate != null) {
            condition = condition
                    .and("jsonb_typeof(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'startDate') = 'number'")
                    .and("cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'startDate' as text)::bigint > 0")
                    .and("and cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'startDate' as text)::timestamp " +
                            ">= to_timestamp('" + startDate.getTime() + "')");
        }
        if (dueDate != null) {
            condition = condition
                    .and("jsonb_typeof(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'dueDate') = 'number'")
                    .and("cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'dueDate' as text)::bigint > 0")
                    .and("and cast(CONTEXT.CONTEXT_DATA -> 'metadata' -> 'dueDate' as text)::timestamp " +
                            ">= to_timestamp('" + dueDate.getTime() + "')");
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
    }

    @Override
    public ContextOwnerEntity findContextOwnerByContextIdAndAssigneeId(UUID contextId, UUID assigneeId) {
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
    }

    @Override
    public List<ContextAssigneeEntity> findContextAssigneeByContextId(UUID contextId){
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.CONTEXT_DATA, CONTEXT.GROUP_ID,
                GROUP_PROFILE.PROFILE_ID.as("assignee_profile_id"))
                .from(CONTEXT)
                .leftJoin(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(CONTEXT.ID.eq(contextId))
                .fetchInto(ContextAssigneeEntity.class);
    }

    private Context insertContext(final Context context) {
        return jooq.insertInto(CONTEXT)
                .set(CONTEXT.ID, UUID.randomUUID())
                .set(CONTEXT.COLLECTION_ID, context.getCollectionId())
                .set(CONTEXT.GROUP_ID, context.getGroupId())
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

}
