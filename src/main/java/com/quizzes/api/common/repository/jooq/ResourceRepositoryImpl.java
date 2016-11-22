package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.quizzes.api.common.model.tables.Context.CONTEXT;
import static com.quizzes.api.common.model.tables.Resource.RESOURCE;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    @Autowired
    private DSLContext jooq;

    public Resource save(final Resource resource) {
        if (resource.getId() == null) {
            return insertResource(resource);
        } else {
            return updateResource(resource);
        }
    }

    public List<Resource> findByCollectionId(UUID collectionId) {
        return jooq.select(RESOURCE.ID, RESOURCE.SEQUENCE, RESOURCE.IS_RESOURCE, RESOURCE.RESOURCE_DATA)
                .from(RESOURCE)
                .where(RESOURCE.COLLECTION_ID.eq(collectionId))
                .and(RESOURCE.IS_DELETED.eq(false))
                .fetchInto(Resource.class);
    }

    @Override
    public Resource findFirstByOrderBySequenceAscByContextId(UUID contextId) {
        return jooq.select(RESOURCE.ID, RESOURCE.SEQUENCE, RESOURCE.IS_RESOURCE, RESOURCE.RESOURCE_DATA)
                .from(RESOURCE)
                .join(CONTEXT).on(CONTEXT.COLLECTION_ID.eq(RESOURCE.COLLECTION_ID))
                .where(CONTEXT.ID.eq(contextId))
                .and(RESOURCE.IS_DELETED.eq(false))
                .fetchAny()
                .into(Resource.class);
    }

    private Resource insertResource(final Resource resource) {
        return jooq.insertInto(RESOURCE)
                .set(RESOURCE.ID, UUID.randomUUID())
                .set(RESOURCE.EXTERNAL_ID, resource.getExternalId())
                .set(RESOURCE.LMS_ID, resource.getLmsId())
                .set(RESOURCE.COLLECTION_ID, resource.getCollectionId())
                .set(RESOURCE.IS_RESOURCE, resource.getIsResource())
                .set(RESOURCE.OWNER_PROFILE_ID, resource.getOwnerProfileId())
                .set(RESOURCE.SEQUENCE, resource.getSequence())
                .set(RESOURCE.RESOURCE_DATA, resource.getResourceData())
                .returning()
                .fetchOne()
                .into(Resource.class);
    }

    private Resource updateResource(final Resource resource) {
        return jooq.update(RESOURCE)
                .set(RESOURCE.RESOURCE_DATA, resource.getResourceData())
                .where(RESOURCE.ID.eq(resource.getId()))
                .returning()
                .fetchOne()
                .into(Resource.class);
    }

}
