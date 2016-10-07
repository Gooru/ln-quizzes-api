package com.quizzes.api.common.service;

import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Profile;
import com.quizzes.api.common.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionNewService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    ProfileService profileService;

    public Collection findByExternalId(String externalId){
        return collectionRepository.findByExternalId(externalId);
    }

    public Collection getOrCreateCollection(String id){
        Collection collection = collectionRepository.findByExternalId(id);
        if(collection == null){
            //TODO: Go to gooru to verify the profile id
            collection = new Collection(id, profileService.findById(UUID.fromString("1399e9bf-075d-43ee-8742-f8f00657fe49")));
            collection = collectionRepository.save(collection);
        }
        return collection;
    }

}
