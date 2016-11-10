package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    public List<Resource> getResourcesByCollectionId(UUID collectionId){
        return resourceRepository.getResourcesByCollectionId(collectionId);

    }
}
