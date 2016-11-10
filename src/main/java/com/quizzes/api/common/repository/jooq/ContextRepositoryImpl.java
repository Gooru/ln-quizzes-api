package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.tables.Context.CONTEXT;

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
        String contextData = "{\"metadata\":{\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"4ef71420-dde9-4d2f-822e-5abb2c0b9c8c\"}}";

        Context context = new Context(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                contextData, null);

        return context;

//        return jooq.select(CONTEXT.ID, CONTEXT.COLLECTION_ID, CONTEXT.GROUP_ID, CONTEXT.CONTEXT_DATA)
//                .from(CONTEXT)
//                .where(CONTEXT.ID.eq(id))
//                .fetchOneInto(Context.class);
    }

    @Override
    public Context mockedFindById(UUID id) {
        Context result = new Context();
        result.setId(UUID.randomUUID());
        result.setCollectionId(UUID.randomUUID());
        result.setGroupId(UUID.randomUUID());
        result.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        return result;
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
    public UUID findCollectionIdByContextId(UUID contextId){
        return UUID.randomUUID();
    }

    private Context insertContext(final Context context) {
        return new Context(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "{\n" +
                "    \"metadata\": {\n" +
                "      \"description\": \"First Partial\",\n" +
                "      \"title\": \"Math 1st Grade\"\n" +
                "    },\n" +
                "    \"contextMap\": {\n" +
                "      \"classId\": \"4ef71420-dde9-4d2f-822e-5abb2c0b9c8c\"\n" +
                "    }\n" +
                "  }", null);
//        return jooq.insertInto(CONTEXT)
//                .set(CONTEXT.ID, UUID.randomUUID())
//                .set(CONTEXT.COLLECTION_ID, context.getCollectionId())
//                .set(CONTEXT.GROUP_ID, context.getGroupId())
//                .set(CONTEXT.CONTEXT_DATA, context.getContextData())
//                .returning()
//                .fetchOne()
//                .into(Context.class);
    }

    private Context updateContext(final Context context) {
        return context;

//        return jooq.update(CONTEXT)
//                .set(CONTEXT.CONTEXT_DATA, context.getContextData())
//                .where(CONTEXT.ID.eq(context.getId()))
//                .returning()
//                .fetchOne()
//                .into(Context.class);
    }

}
