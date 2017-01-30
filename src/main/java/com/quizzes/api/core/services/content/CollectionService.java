package com.quizzes.api.core.services.content;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
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


    /**
     * Creates a new {@link Collection} in Quizzes based on the content's provider Assessment
     * if the assessment belongs to a different owner then the assessment is copied
     * and the new {@link Collection} is based on that copy
     *
     * @param externalCollectionId content's provider Assessment ID
     * @param owner                the Quizzes {@link Profile} of the {@link Collection} owner
     * @return the Quizzes new {@link Collection}
     */
    public Collection createCollection(String externalCollectionId, Profile owner) {
        UserDataTokenDto userDataTokenDto = gson.fromJson(owner.getProfileData(), UserDataTokenDto.class);
        String userToken = authenticationRestClient.generateUserToken(userDataTokenDto);
//        AssessmentContentDto assessmentDto = collectionRestClient.getCollection(externalCollectionId, userToken);
        AssessmentContentDto assessmentDto = new AssessmentContentDto();

        Collection result = null;
        if (assessmentDto.getOwnerId() != null) {
            if (assessmentDto.getOwnerId().equals(owner.getExternalId())) {
                result = createCollectionFromAssessment(assessmentDto, externalCollectionId, owner.getId());
            } else {
                result = createCollectionCopy(externalCollectionId, owner.getId(), userToken);
            }
        }

        return result;
    }

    /**
     * Copies an Assessment in the content provider and creates a new {@link Collection} in Quizzes
     * based on that copied Assessment
     *
     * @param assessmentId ID of the Assessment in the content provider
     * @param ownerId      ID of the owner of the {@link Collection} in Quizzes
     * @param userToken    the content provider's authorization token
     * @return the new {@link Collection} in Quizzes
     */
    private Collection createCollectionCopy(String assessmentId, UUID ownerId, String userToken) {

        String copiedAssessmentId = collectionRestClient.copyAssessment(assessmentId, userToken);

//        AssessmentContentDto assessmentDto = collectionRestClient.getCollection(copiedAssessmentId, userToken);
        AssessmentContentDto assessmentDto = new AssessmentContentDto();

        return createCollectionFromAssessment(assessmentDto, assessmentId, ownerId);
    }

    /**
     * Creates a new {@link Collection} in Quizzes based on the content's provider {@link AssessmentContentDto}
     * In the content provider the Assessments can be a copy of another user's Assessment
     * or an original Assessment created by it's owner.
     * If the parent assessment ID is the same as the assessment ID then this is an original assessment,
     * but if the IDs are different then this is a copy of an existing assessment in the content provider
     *
     * @param assessmentDto      content's provider Assessment information
     * @param parentAssessmentId ID of the parent Assessment in the content provider
     * @param ownerId            ID of the Quizzes owner
     * @return The Quizzes {@link Collection}
     */
    private Collection createCollectionFromAssessment(AssessmentContentDto assessmentDto, String parentAssessmentId, UUID ownerId) {
        Collection collection = new Collection();
        // TODO: The logic to obtain the correct external_id and external_parent_id must be implemented
        collection.setExternalId(assessmentDto.getId());
        collection.setExternalParentId(parentAssessmentId);
        collection.setContentProvider(ContentProvider.gooru);
        collection.setOwnerProfileId(ownerId);
        collection.setIsCollection(false);
        collection.setIsLocked(false);
        Map<String, Object> collectionDataMap = new HashMap<>();
        collectionDataMap.put(COLLECTION_TITLE, assessmentDto.getTitle());
        collection.setCollectionData(new Gson().toJson(collectionDataMap));

        //collection = collectionService.save(collection);

        copyQuestions(collection, ownerId, assessmentDto.getQuestions());

        return collection;
    }

    private void copyQuestions(Collection collection, UUID ownerId, List<ResourceContentDto> questions) {
        if (questions != null) {
            for (ResourceContentDto questionDto : questions) {
                Resource resource = new Resource();
                resource.setExternalId(questionDto.getId());
                resource.setContentProvider(ContentProvider.gooru);
                resource.setCollectionId(collection.getId());
                resource.setOwnerProfileId(ownerId);
                resource.setIsResource(false);
                resource.setSequence((short) questionDto.getSequence());
                Map<String, Object> resourceDataMap = new HashMap<>();

                resourceDataMap.put(QUESTION_TITLE, questionDto.getTitle());
                resourceDataMap.put(QUESTION_TYPE, mapQuestionType(questionDto.getContentSubformat()));
                resourceDataMap.put(QUESTION_CORRECT_ANSWER, getCorrectAnswers(questionDto.getAnswers()));
                resourceDataMap.put(QUESTION_BODY, questionDto.getTitle());
                resourceDataMap.put(QUESTION_INTERACTION, createInteraction(questionDto.getAnswers()));
                resource.setResourceData(new Gson().toJson(resourceDataMap));
                //resourceService.save(resource);
            }
        }
    }

    private Map<String, Object> createInteraction(List<AnswerContentDto> answers) {
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
