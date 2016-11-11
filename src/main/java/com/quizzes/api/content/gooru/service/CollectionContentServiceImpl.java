package com.quizzes.api.content.gooru.service;

import com.google.gson.Gson;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AnswerDto;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.dto.QuestionDto;
import com.quizzes.api.content.gooru.enums.GooruQuestionTypeEnum;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionContentServiceImpl implements CollectionContentService {

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



    @Override
    public Collection createCollectionCopy(String externalCollectionId, Profile owner) {
        AssessmentDto assessmentDto = collectionRestClient.getAssessment(externalCollectionId);

        Collection collection = new Collection();
        collection.setExternalId(assessmentDto.getId());
        collection.setLmsId(Lms.gooru);
        collection.setOwnerProfileId(owner.getId());
        collection.setIsCollection(false);
        Map<String, Object> collectionDataMap = new HashMap<>();
        collectionDataMap.put("title", assessmentDto.getTitle());
        collection.setCollectionData(new Gson().toJson(collectionDataMap));

        collection = collectionService.save(collection);

        for(QuestionDto questionDto : assessmentDto.getQuestions()) {
            Resource resource = new Resource();
            resource.setExternalId(questionDto.getId());
            resource.setLmsId(Lms.gooru);
            resource.setOwnerProfileId(owner.getId());
            resource.setIsResource(false);
            resource.setSequence(questionDto.getSequence());
            Map<String, Object> resourceDataMap = new HashMap<>();
            resourceDataMap.put("title", questionDto.getTitle());
            resourceDataMap.put("type", mapQuestionType(questionDto.getContentSubformat()));
            resourceDataMap.put("correctAnswer", getCorrectAnswers(questionDto.getAnswers()));
            resource.setResourceData(new Gson().toJson(resourceDataMap));

            // TODO save the resource
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
                .filter(answer -> answer.isCorrect())
                .map(answer -> {
                    Map<String, String> answerValue = new HashMap<>();
                    answerValue.put("value", answer.getAnswerText());
                    return answerValue;
                })
                .collect(Collectors.toList());
    }

}
