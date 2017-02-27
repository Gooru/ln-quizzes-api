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

    public CollectionDto getAssessment(UUID assessmentId, boolean withCacheRefresh) {
        return withCacheRefresh ?
                collectionRestClient.getAssessmentWithCacheRefresh(assessmentId) :
                collectionRestClient.getAssessment(assessmentId);
    }

    public CollectionDto getAssessment(UUID assessmentId) {
        return getAssessment(assessmentId, false);
    }

    public CollectionDto getCollection(UUID collectionId, boolean withCacheRefresh) {
        return withCacheRefresh ?
                collectionRestClient.getCollectionWithCacheRefresh(collectionId) :
                collectionRestClient.getCollection(collectionId);
    }

    public CollectionDto getCollection(UUID collectionId) {
        return getCollection(collectionId, false);
    }

    public CollectionDto getCollectionOrAssessment(UUID collectionId) {
        try {
            return getCollection(collectionId);
        } catch (ContentNotFoundException e) {
            return getAssessment(collectionId);
        }
    }

    public List<ResourceDto> getAssessmentQuestions(UUID assessmentId) {
        CollectionDto assessment = getAssessment(assessmentId);
        return assessment.getResources();
    }

    public List<ResourceDto> getCollectionResources(UUID collectionId) {
        CollectionDto collection = getCollection(collectionId);
        return collection.getResources();
    }

}
