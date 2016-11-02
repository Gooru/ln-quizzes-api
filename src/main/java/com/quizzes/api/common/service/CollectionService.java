package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.realtime.model.CollectionOnAir;
import org.springframework.stereotype.Service;

@Service
public interface CollectionService {

    Collection findByExternalIdAndLmsId(String externalId, Lms lms);

    Collection save(Collection collection);

    //TODO: WE NEED TO REMOVE THIS OLD METHODS - OLD REAL TIME METHODS

    CollectionOnAir findCollectionOnAir(String classId, String collectionId);

    Iterable<CollectionOnAir> findCollectionsOnAirByClass(String classId);

    CollectionOnAir addCollectionOnAir(String classId, String collectionId);

    void removeCollectionOnAir(String classId, String collectionId);

    void completeCollectionForUser(String collectionUniqueId, String userId);

    void resetCollectionForUser(String collectionUniqueId, String userId);

}