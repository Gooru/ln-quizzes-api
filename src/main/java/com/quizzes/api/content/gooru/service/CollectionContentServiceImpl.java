package com.quizzes.api.content.gooru.service;

import com.google.gson.Gson;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ResourceService;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AnswerDto;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.dto.QuestionDto;
import com.quizzes.api.content.gooru.enums.GooruQuestionTypeEnum;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionContentServiceImpl implements CollectionContentService {

    private static final String COLLECTION_TITLE = "title";
    private static final String RESOURCE_TITLE = "title";
    private static final String RESOURCE_TYPE = "type";
    private static final String RESOURCE_CORRECT_ANSWER = "correctAnswer";
    private static final String ANSWER_VALUE = "value";

    private static final Map<String, String> questionTypeMap;

    static {
        questionTypeMap = new HashMap<>();
        questionTypeMap.put(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral(),
                QuestionTypeEnum.TrueFalse.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral(),
                QuestionTypeEnum.SingleChoice.getLiteral());
    }

    @Autowired
    CollectionRestClient collectionRestClient;

    @Autowired
    CollectionService collectionService;

    @Autowired
    ResourceService resourceService;

    @Override
    public Collection createCollectionCopy(String externalCollectionId, Profile owner) {
        AssessmentDto assessmentDto = collectionRestClient.getAssessment(externalCollectionId);

        Collection collection = new Collection();
        collection.setExternalId(assessmentDto.getId());
        collection.setLmsId(Lms.gooru);
        collection.setOwnerProfileId(owner.getId());
        collection.setIsCollection(false);
        collection.setIsLock(false);
        Map<String, Object> collectionDataMap = new HashMap<>();
        collectionDataMap.put(COLLECTION_TITLE, assessmentDto.getTitle());
        collection.setCollectionData(new Gson().toJson(collectionDataMap));

        collection = collectionService.save(collection);

        for(QuestionDto questionDto : assessmentDto.getQuestions()) {
            Resource resource = new Resource();
            resource.setExternalId(questionDto.getId());
            resource.setLmsId(Lms.gooru);
            resource.setCollectionId(collection.getId());
            resource.setOwnerProfileId(owner.getId());
            resource.setIsResource(false);
            resource.setSequence((short) questionDto.getSequence());
            Map<String, Object> resourceDataMap = new HashMap<>();
            resourceDataMap.put(RESOURCE_TITLE, questionDto.getTitle());
            resourceDataMap.put(RESOURCE_TYPE, mapQuestionType(questionDto.getContentSubformat()));
            resourceDataMap.put(RESOURCE_CORRECT_ANSWER, getCorrectAnswers(questionDto.getAnswers()));
            resource.setResourceData(new Gson().toJson(resourceDataMap));

            resourceService.save(resource);
        }

        return collection;
    }

    private String mapQuestionType(String gooruQuestionType) {
        String mappedType = questionTypeMap.get(gooruQuestionType);
        if (mappedType == null) {
            mappedType = QuestionTypeEnum.None.getLiteral();
        }
        return mappedType;
    }

    private List<Map<String, String>> getCorrectAnswers(List<AnswerDto> answers) {
        return answers.stream()
                .filter(answer -> answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1"))
                .map(answer -> {
                    Map<String, String> answerValue = new HashMap<>();
                    answerValue.put(ANSWER_VALUE, answer.getAnswerText());
                    return answerValue;
                })
                .collect(Collectors.toList());
    }

}
