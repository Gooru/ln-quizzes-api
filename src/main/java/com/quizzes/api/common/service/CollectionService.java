package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.response.CollectionDataDto;
import com.quizzes.api.common.dto.controller.response.CollectionDataResourceDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.CollectionRepository;
import com.quizzes.api.realtime.model.CollectionOnAir;
import com.quizzes.api.realtime.repository.CollectionOnAirRepository;
import com.quizzes.api.realtime.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Autowired
    JsonParser jsonParser;

    //TODO: Tests
    public Collection findByExternalId(String externalId) {
        return collectionRepository.findByExternalId(externalId);
    }

    //TODO: tests
    public Collection save(Collection collection) {
        return collectionRepository.save(collection);
    }

    public Collection findById(UUID id) {
        return collectionRepository.findById(id);
    }

    public CollectionDataDto getCollection(UUID collectionId) {
        CollectionDataDto result = null;
        Collection collection = collectionRepository.findById(collectionId);
        if (collection != null) {
            result = new CollectionDataDto();
            result.setId(collectionId);
            result.setIsCollection(collection.getIsCollection());
            List<Resource> resources = resourceService.findByCollectionId(collectionId);
            List<CollectionDataResourceDto> resourceList =
                    resources.stream().map(resource -> {
                        CollectionDataResourceDto dataResourceDto = new CollectionDataResourceDto();
                        dataResourceDto.setId(resource.getId());
                        dataResourceDto.setIsResource(resource.getIsResource());
                        dataResourceDto.setSequence(resource.getSequence());
                        dataResourceDto.setQuestions(jsonParser.parseMap(resource.getResourceData()));
                        return dataResourceDto;
                    }).collect(Collectors.toList());
            result.setResources(resourceList);
        }
        return result;
    }

    //TODO: WE NEED TO REMOVE THIS OLD METHODS - OLD REAL TIME METHODS
    @Autowired
    private EventService eventService;

    @Autowired
    private CollectionOnAirRepository collectionOnAirRepository;


    public CollectionOnAir findCollectionOnAir(String classId, String collectionId) {
        return collectionOnAirRepository.findFirstByClassIdAndCollectionId(classId, collectionId);
    }

    public Iterable<CollectionOnAir> findCollectionsOnAirByClass(String classId) {
        return collectionOnAirRepository.findByClassId(classId);
    }

    public CollectionOnAir addCollectionOnAir(String classId, String collectionId) {
        CollectionOnAir collectionOnAir = findCollectionOnAir(classId, collectionId);
        if (Objects.isNull(collectionOnAir)) {
            collectionOnAir = collectionOnAirRepository.save(new CollectionOnAir(classId, collectionId));
        }
        return collectionOnAir;
    }

    public void removeCollectionOnAir(String classId, String collectionId) {
        CollectionOnAir collectionOnAir = findCollectionOnAir(classId, collectionId);
        if (Objects.nonNull(collectionOnAir)) {
            collectionOnAirRepository.delete(collectionOnAir);
        }
    }

    public void completeCollectionForUser(String collectionUniqueId, String userId) {
        eventService.completeEventIndexByUser(collectionUniqueId, userId);
    }

    public void resetCollectionForUser(String collectionUniqueId, String userId) {
        eventService.deleteCollectionEventsByUser(collectionUniqueId, userId);
    }

}
