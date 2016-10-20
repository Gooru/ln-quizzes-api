package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.repository.CollectionRepository;
import com.quizzes.api.realtime.model.CollectionOnAir;
import com.quizzes.api.realtime.repository.CollectionOnAirRepository;
import com.quizzes.api.realtime.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    ProfileServiceImpl profileServiceImpl;

    public Collection findByExternalId(String externalId) {
        return collectionRepository.findByExternalId(externalId);
    }

    public Collection findOrCreateCollection(CollectionDTO id) {
        return null;
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
