package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Resource;
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

    public List<Resource> findByCollectionId(UUID collectionId){
        return resourceRepository.findByCollectionId(collectionId);
    }

    public Resource findFirstByOrderBySequenceAscByContextId(UUID contextId){
        return resourceRepository.findFirstByOrderBySequenceAscByContextId(contextId);
    }
}
