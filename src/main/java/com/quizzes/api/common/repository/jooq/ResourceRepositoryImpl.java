package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    public List<Resource> getResourcesByCollectionId(UUID collectionId){
        List<Resource> resources = new ArrayList<>();
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setIsResource(true);
        
        return resources;
    }

}
