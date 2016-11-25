package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.quizzes.api.common.model.tables.Context.CONTEXT;
import static com.quizzes.api.common.model.tables.Group.GROUP;
import static com.quizzes.api.common.model.tables.GroupProfile.GROUP_PROFILE;

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
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(id))
                .fetchOneInto(Context.class);
    }

    @Override
    public List<Context> findByOwnerId(UUID ownerId) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchInto(Context.class);
    }

    @Override
    public Map<UUID, List<ContextAssigneeEntity>> findContextAssigneeByOwnerId(UUID ownerId){
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA,
                GROUP_PROFILE.PROFILE_ID.as("assigneeProfileId"))
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP.OWNER_PROFILE_ID.eq(ownerId))
                .fetchGroups(CONTEXT.ID, ContextAssigneeEntity.class);
    }

    @Override
    public Context findByCollectionIdAndGroupId(UUID collectionId, UUID groupId) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
                .from(CONTEXT)
                .where(CONTEXT.COLLECTION_ID.eq(collectionId))
                .and(CONTEXT.GROUP_ID.eq(groupId))
                .fetchOneInto(Context.class);
    }

    @Override
    public UUID findCollectionIdByContextId(UUID contextId) {
        return jooq.select(CONTEXT.COLLECTION_ID)
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(contextId))
                .fetchOneInto(UUID.class);
    }

    @Override
    public List<ContextOwnerEntity> findContextOwnerByAssigneeId(UUID assigneeId) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.CONTEXT_DATA, CONTEXT.CREATED_AT, GROUP.OWNER_PROFILE_ID)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(CONTEXT.GROUP_ID))
                .where(GROUP_PROFILE.PROFILE_ID.eq(assigneeId))
                .fetchInto(ContextOwnerEntity.class);
    }

    @Override
    public ContextOwnerEntity findContextOwnerByContextId(UUID contextId) {
        return jooq.select(CONTEXT.COLLECTION_ID, CONTEXT.CONTEXT_DATA, GROUP.OWNER_PROFILE_ID)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .where(CONTEXT.ID.eq(contextId))
                .fetchOneInto(ContextOwnerEntity.class);
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
