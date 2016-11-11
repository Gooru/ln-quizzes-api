package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    public List<Resource> findResourcesByCollectionId(UUID collectionId){
        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setId(UUID.randomUUID());
        resource1.setIsResource(false);
        resource1.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"SingleChoice\"," +
                "\"correctAnswer\": [{\"value\": \"A\"}],\"body\": \"mocked body\",\"interaction\":" +
                " {\"shuffle\": true,\"maxChoices\": 10,\"prompt\": \"mocked Interaction\",\"choices\":" +
                " [{\"text\": \"option 1\",\"isFixed\": false,\"value\": \"A\"},{\"text\": \"option 2\",\"isFixed\":" +
                " false,\"value\": \"B\"},{\"text\": \"option 3\",\"isFixed\": false,\"value\": \"C\"}]}}");
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setId(UUID.randomUUID());
        resource2.setIsResource(false);
        resource2.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"True/False\",\"correctAnswer\":" +
                " [{\"value\": \"T\"}],\"body\": \"mocked body\",\"interaction\": {\"shuffle\": true,\"maxChoices\":" +
                " 10,\"prompt\": \"mocked Interaction\",\"choices\": [{\"text\": \"True\",\"isFixed\": false,\"value\": " +
                "\"T\"},{\"text\": \"False\",\"isFixed\": false,\"value\": \"F\"}]}}");
        resources.add(resource2);
        return resources;
    }

}
