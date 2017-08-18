package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.RubricDto;
import com.quizzes.api.core.dtos.content.QuestionContentDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CollectionService {

    @Autowired
    private CollectionRestClient collectionRestClient;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RubricService rubricService;

    public CollectionDto getAssessment(UUID assessmentId, boolean withCacheRefresh, String authToken) {
        CollectionDto collectionDto = withCacheRefresh ?
                collectionRestClient.getAssessmentWithCacheRefresh(assessmentId, authToken) :
                collectionRestClient.getAssessment(assessmentId, authToken);

        getQuestionRubrics(collectionDto, authToken);

        return collectionDto;
    }

    public CollectionDto getAssessment(UUID assessmentId, String authToken) {
        return getAssessment(assessmentId, false, authToken);
    }

    public CollectionDto getCollection(UUID collectionId, boolean withCacheRefresh, String authToken) {
        CollectionDto collectionDto = withCacheRefresh ?
               collectionRestClient.getCollectionWithCacheRefresh(collectionId, authToken) :
               collectionRestClient.getCollection(collectionId, authToken);

        getQuestionRubrics(collectionDto, authToken);

        return collectionDto;
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

    private CollectionDto getQuestionRubrics(CollectionDto collectionDto, String authToken) {
        if (collectionDto.getResources() != null) {
            collectionDto.getResources().forEach(resourceDto -> {
                if (resourceDto.getMetadata() != null &&
                        resourceDto.getMetadata().getType().equals(QuestionTypeEnum.ExtendedText.getLiteral())) {

                    try {
                        QuestionContentDto questionContentDto = questionService.getQuestion(resourceDto.getId(), authToken);
                        if (questionContentDto.getRubric() != null) {
                            RubricDto rubricDto = rubricService.getRubric(questionContentDto.getRubric().getId(), authToken);
                            resourceDto.setRubric(rubricDto);
                        }
                    } catch (Exception exception) {
                        log.warn("Unable to retrieve rubric for question " + resourceDto.getId(), exception);
                    }
                }
            });
        }

        return collectionDto;
    }

}
