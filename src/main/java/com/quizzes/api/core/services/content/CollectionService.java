package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionService {

    @Autowired
    private CollectionRestClient collectionRestClient;

    public CollectionDto getAssessment(UUID assessmentId, boolean withCacheRefresh, String authToken) {
        return withCacheRefresh ?
                collectionRestClient.getAssessmentWithCacheRefresh(assessmentId, authToken) :
                collectionRestClient.getAssessment(assessmentId, authToken);
    }

    public CollectionDto getAssessment(UUID assessmentId, String authToken) {
        return getAssessment(assessmentId, false, authToken);
    }

    public CollectionDto getCollection(UUID collectionId, boolean withCacheRefresh, String authToken) {
        return withCacheRefresh ?
               collectionRestClient.getCollectionWithCacheRefresh(collectionId, authToken) :
               collectionRestClient.getCollection(collectionId, authToken);
    }

    public CollectionDto getCollection(UUID collectionId, String authToken) {
        return getCollection(collectionId, false, authToken);
    }

    public CollectionDto getCollectionOrAssessment(UUID collectionId, String authToken) {
        try {
            return getCollection(collectionId, authToken);
        } catch (ContentNotFoundException e) {
            return getAssessment(collectionId, authToken);
        }
    }

    public CollectionDto getCollectionOrAssessment(UUID collectionId, Boolean isCollection, String authToken) {
        return (isCollection == null) ?
                getCollectionOrAssessment(collectionId, authToken) :
                (isCollection) ? getCollection(collectionId, authToken) : getAssessment(collectionId, authToken);
    }

    public List<ResourceDto> getAssessmentQuestions(UUID assessmentId, String authToken) {
        CollectionDto assessment = getAssessment(assessmentId, authToken);
        return assessment.getResources();
    }

    public List<ResourceDto> getCollectionResources(UUID collectionId, String authToken) {
        CollectionDto collection = getCollection(collectionId, authToken);
        return collection.getResources();
    }

}
