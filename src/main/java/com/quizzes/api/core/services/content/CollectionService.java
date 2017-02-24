package com.quizzes.api.core.services.content;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReturnDataUpdateContent;
import com.google.code.ssm.api.UpdateSingleCache;
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

    public CollectionDto getAssessment(UUID assessmentId) {
        return collectionRestClient.getAssessment(assessmentId);
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Assessments")
    public CollectionDto getAssessmentWithCacheRefresh(@ParameterValueKeyProvider UUID assessmentId) {
        return getAssessment(assessmentId);
    }

    public CollectionDto getCollection(UUID collectionId) {
        return collectionRestClient.getCollection(collectionId);
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Collections")
    public CollectionDto getCollectionWithCacheRefresh(@ParameterValueKeyProvider UUID collectionId) {
        return getCollection(collectionId);
    }

    public CollectionDto getCollectionOrAssessment(UUID collectionId) {
        try {
            return getCollection(collectionId);
        } catch (ContentNotFoundException e){
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
