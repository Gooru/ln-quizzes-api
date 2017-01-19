package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource findById(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId);
        if (resource == null) {
            throw new ContentNotFoundException("Not Found Resource Id: " + resourceId);
        }
        return resource;
    }

    public List<Resource> findByCollectionId(UUID collectionId) {
        return resourceRepository.findByCollectionId(collectionId);
    }

    public Resource findFirstByContextIdOrderBySequence(UUID contextId) {
        return resourceRepository.findFirstByContextIdOrderBySequence(contextId);
    }
}
