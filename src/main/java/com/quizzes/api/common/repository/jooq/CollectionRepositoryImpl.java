package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.repository.CollectionRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.tables.Collection.COLLECTION;

@Repository
public class CollectionRepositoryImpl implements CollectionRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Collection findByExternalIdAndLmsId(String externalId, Lms lmsId) {
        return jooq.select(COLLECTION.ID, COLLECTION.EXTERNAL_ID, COLLECTION.LMS_ID, COLLECTION.COLLECTION_DATA,
                COLLECTION.IS_COLLECTION, COLLECTION.OWNER_PROFILE_ID, COLLECTION.IS_LOCK)
                .from(COLLECTION)
                .where(COLLECTION.EXTERNAL_ID.eq(externalId))
                .and(COLLECTION.LMS_ID.eq(lmsId))
                .fetchOneInto(Collection.class);
    }

    @Override
    public Collection save(final Collection collection) {
        if (collection.getId() == null) {
            return insertCollection(collection);
        } else {
            return updateCollection(collection);
        }
    }

    private Collection insertCollection(final Collection collection) {
        return jooq.insertInto(COLLECTION)
                .set(COLLECTION.ID, UUID.randomUUID())
                .set(COLLECTION.EXTERNAL_ID, collection.getExternalId())
                .set(COLLECTION.LMS_ID, collection.getLmsId())
                .set(COLLECTION.IS_COLLECTION, collection.getIsCollection())
                .set(COLLECTION.OWNER_PROFILE_ID, collection.getOwnerProfileId())
                .set(COLLECTION.COLLECTION_DATA, collection.getCollectionData())
                .set(COLLECTION.IS_LOCK, collection.getIsLock())
                .returning()
                .fetchOne()
                .into(Collection.class);
    }

    private Collection updateCollection(final Collection collection) {
        return jooq.update(COLLECTION)
                .set(COLLECTION.COLLECTION_DATA, collection.getCollectionData())
                .where(COLLECTION.ID.eq(collection.getId()))
                .returning()
                .fetchOne()
                .into(Collection.class);
    }

}