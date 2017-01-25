package com.quizzes.api.core.repositories.jooq;

import com.quizzes.api.core.model.jooq.tables.pojos.Collection;
import com.quizzes.api.core.repositories.CollectionRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.core.model.jooq.tables.Collection.COLLECTION;

@Repository
public class CollectionRepositoryImpl implements CollectionRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Collection findByExternalId(String externalId) {
        return jooq.select(COLLECTION.ID, COLLECTION.EXTERNAL_ID, COLLECTION.CONTENT_PROVIDER, COLLECTION.COLLECTION_DATA,
                COLLECTION.IS_COLLECTION, COLLECTION.OWNER_PROFILE_ID, COLLECTION.IS_LOCKED)
                .from(COLLECTION)
                .where(DSL.condition("DECODE(MD5(EXTERNAL_ID), 'HEX') = DECODE(MD5(?), 'HEX')", externalId))
                .and(COLLECTION.EXTERNAL_ID.eq(externalId))
                .fetchOneInto(Collection.class);
    }

    @Override
    public Collection findByOwnerProfileIdAndExternalParentId(UUID ownerProfileId, String externalParentId) {
        return jooq.select(COLLECTION.ID, COLLECTION.EXTERNAL_ID, COLLECTION.CONTENT_PROVIDER, COLLECTION.COLLECTION_DATA,
                COLLECTION.IS_COLLECTION, COLLECTION.OWNER_PROFILE_ID, COLLECTION.IS_LOCKED)
                .from(COLLECTION)
                .where(COLLECTION.OWNER_PROFILE_ID.eq(ownerProfileId))
                .and(DSL.condition("DECODE(MD5(EXTERNAL_PARENT_ID), 'HEX') = DECODE(MD5(?), 'HEX')", externalParentId))
                .and(COLLECTION.EXTERNAL_PARENT_ID.eq(externalParentId))
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

    @Override
    public Collection findById(UUID collectionId) {
        return jooq.select(COLLECTION.ID, COLLECTION.COLLECTION_DATA, COLLECTION.IS_COLLECTION)
                .from(COLLECTION)
                .where(COLLECTION.ID.eq(collectionId))
                .fetchOneInto(Collection.class);
    }

    private Collection insertCollection(final Collection collection) {
        return jooq.insertInto(COLLECTION)
                .set(COLLECTION.ID, UUID.randomUUID())
                .set(COLLECTION.EXTERNAL_ID, collection.getExternalId())
                .set(COLLECTION.EXTERNAL_PARENT_ID, collection.getExternalParentId())
                .set(COLLECTION.CONTENT_PROVIDER, collection.getContentProvider())
                .set(COLLECTION.IS_COLLECTION, collection.getIsCollection())
                .set(COLLECTION.OWNER_PROFILE_ID, collection.getOwnerProfileId())
                .set(COLLECTION.COLLECTION_DATA, collection.getCollectionData())
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
