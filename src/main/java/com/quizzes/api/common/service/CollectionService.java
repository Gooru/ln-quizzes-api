package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.CollectionGetResponseDto;
import com.quizzes.api.common.dto.QuestionDataDto;
import com.quizzes.api.common.dto.ResourceDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    Gson gson;

    public Collection findByExternalId(String externalId) {
        return collectionRepository.findByExternalId(externalId);
    }

    public Collection findByOwnerProfileIdAndExternalParentId(UUID ownerProfileId, String externalParentId) {
        return collectionRepository.findByOwnerProfileIdAndExternalParentId(ownerProfileId, externalParentId);
    }

    public CollectionGetResponseDto findCollectionById(UUID collectionId) throws ContentNotFoundException {
        Collection collection = collectionRepository.findById(collectionId);
        if (collection == null) {
            throw new ContentNotFoundException("Collection not found for ID: " + collectionId);
        }

        CollectionGetResponseDto response = new CollectionGetResponseDto();
        response.setId(collection.getId());
        response.setIsCollection(collection.getIsCollection());
        response.setResources(resourceService.findByCollectionId(collection.getId()).stream()
                .map(resource -> {
                    ResourceDto dataResourceDto = new ResourceDto();
                    dataResourceDto.setId(resource.getId());
                    dataResourceDto.setIsResource(resource.getIsResource());
                    dataResourceDto.setSequence(resource.getSequence());
                    dataResourceDto.setQuestionData(gson.fromJson(resource.getResourceData(), QuestionDataDto.class));
                    return dataResourceDto;
                }).collect(Collectors.toList()));

        return response;
    }

    public Collection save(Collection collection) {
        return collectionRepository.save(collection);
    }

}
