package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<Resource> findResourcesByCollectionId(UUID collectionId){
        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setId(UUID.randomUUID());
        resource1.setIsResource(false);
        resource1.setSequence((short) 1);
        resource1.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"single_choice\"," +
                "\"correctAnswer\": [{\"value\": \"A\"}],\"body\": \"mocked body\",\"interaction\":" +
                " {\"shuffle\": true,\"maxChoices\": 10,\"prompt\": \"mocked Interaction\",\"choices\":" +
                " [{\"text\": \"option 1\",\"isFixed\": false,\"value\": \"A\"},{\"text\": \"option 2\",\"isFixed\":" +
                " false,\"value\": \"B\"},{\"text\": \"option 3\",\"isFixed\": false,\"value\": \"C\"}]}}");
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setId(UUID.randomUUID());
        resource2.setIsResource(false);
        resource2.setSequence((short) 2);
        resource2.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"true_false\",\"correctAnswer\":" +
                " [{\"value\": \"T\"}],\"body\": \"mocked body\",\"interaction\": {\"shuffle\": true,\"maxChoices\":" +
                " 10,\"prompt\": \"mocked Interaction\",\"choices\": [{\"text\": \"True\",\"isFixed\": false,\"value\": " +
                "\"T\"},{\"text\": \"False\",\"isFixed\": false,\"value\": \"F\"}]}}");
        resources.add(resource2);
        return resources;
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
