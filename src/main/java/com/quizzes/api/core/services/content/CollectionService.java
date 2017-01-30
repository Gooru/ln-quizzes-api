package com.quizzes.api.core.services.content;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.dtos.content.AnswerDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.QuestionContentDto;
import com.quizzes.api.core.dtos.content.UserDataTokenDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.jooq.enums.ContentProvider;
import com.quizzes.api.core.model.jooq.tables.pojos.Collection;
import com.quizzes.api.core.model.jooq.tables.pojos.Profile;
import com.quizzes.api.core.model.jooq.tables.pojos.Resource;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private static final String COLLECTION_TITLE = "title";
    private static final String QUESTION_TITLE = "title";
    private static final String QUESTION_TYPE = "type";
    private static final String QUESTION_CORRECT_ANSWER = "correctAnswer";
    private static final String QUESTION_BODY = "body";
    private static final String QUESTION_INTERACTION = "interaction";
    private static final String INTERACTION_SHUFFLE = "shuffle";
    private static final String INTERACTION_MAX_CHOICES = "maxChoices";
    private static final String INTERACTION_PROMPT = "prompt";
    private static final String INTERACTION_CHOICES = "choices";
    private static final String CHOICE_TEXT = "text";
    private static final String CHOICE_VALUE = "value";
    private static final String CHOICE_IS_FIXED = "isFixed";
    private static final String CHOICE_SEQUENCE = "sequence";


    private static final String ANSWER_VALUE = "value";

    private static final Map<String, String> questionTypeMap;

    static {
        questionTypeMap = new HashMap<>();
        questionTypeMap.put(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral(),
                QuestionTypeEnum.TrueFalse.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral(),
                QuestionTypeEnum.SingleChoice.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral(),
                QuestionTypeEnum.DragAndDrop.getLiteral());
    }

    @Autowired
    CollectionRestClient collectionRestClient;

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Autowired
    Gson gson;


    public CollectionGetResponseDto findCollectionById(UUID collectionId) throws ContentNotFoundException {
        return new CollectionGetResponseDto();
    }

    private Map<String, Object> createInteraction(List<AnswerDto> answers) {
        Map<String, Object> interactionDataMap = new HashMap<>();
        interactionDataMap.put(INTERACTION_SHUFFLE, false);
        interactionDataMap.put(INTERACTION_MAX_CHOICES, 0);
        interactionDataMap.put(INTERACTION_PROMPT, "");

        List<Map<String, Object>> choices = new ArrayList<>();
        if (answers != null) {
            choices = answers.stream()
                    .map(answer -> {
                        Map<String, Object> choiceDataMap = new HashMap<>();
                        choiceDataMap.put(CHOICE_TEXT, answer.getAnswerText());
                        choiceDataMap.put(CHOICE_VALUE, answer.getId());
                        choiceDataMap.put(CHOICE_SEQUENCE, answer.getSequence());
                        choiceDataMap.put(CHOICE_IS_FIXED, true);
                        return choiceDataMap;
                    })
                    .collect(Collectors.toList());
        }
        interactionDataMap.put(INTERACTION_CHOICES, choices);

        return interactionDataMap;
    }

    private String mapQuestionType(String gooruQuestionType) {
        String mappedType = questionTypeMap.get(gooruQuestionType);
        if (mappedType == null) {
            mappedType = QuestionTypeEnum.None.getLiteral();
        }
        return mappedType;
    }

    private List<Map<String, String>> getCorrectAnswers(List<AnswerContentDto> answers) {
        List<Map<String, String>> correctAnswers = new ArrayList<>();
        if (answers != null) {
            correctAnswers = answers.stream()
                    .filter(answer -> answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1"))
                    .map(answer -> {
                        Map<String, String> answerValue = new HashMap<>();
                        answerValue.put(ANSWER_VALUE, answer.getId());
                        return answerValue;
                    })
                    .collect(Collectors.toList());
        }
        return correctAnswers;
    }

}
