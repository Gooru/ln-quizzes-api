package com.quizzes.api.common.repository;

import static com.quizzes.api.common.model.tables.Context.CONTEXT;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.records.ContextRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContextRepositoryImpl implements ContextRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Context save(Context context) {
        if (context.getId() == null) {
            context.setId(UUID.randomUUID());
            ContextRecord contextRecord = jooq.newRecord(CONTEXT, context);
            jooq.executeInsert(contextRecord);
        } else {
            ContextRecord contextRecord = jooq.newRecord(CONTEXT, context);
            jooq.update(CONTEXT).set(contextRecord).where(CONTEXT.ID.eq(contextRecord.getId())).execute();
        }
        return context;
    }

    @Override
    public Context findById(UUID id) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
                .from(CONTEXT)
                .where(CONTEXT.ID.eq(id))
                .fetchAny().into(Context.class);
    }

    @Override
    public Context findByCollectionIdAndGroupId(UUID collectionId, UUID groupId) {
        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
                .from(CONTEXT)
                .where(CONTEXT.COLLECTION_ID.eq(collectionId))
                .and(CONTEXT.GROUP_ID.eq(groupId))
                .fetchAny().into(Context.class);
    }

}
