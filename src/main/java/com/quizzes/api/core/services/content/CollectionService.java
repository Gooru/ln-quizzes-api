package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.CollectionMetadataDto;
import com.quizzes.api.core.dtos.InteractionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private static final Map<String, String> questionTypeMap;

    static {
        questionTypeMap = new HashMap<>();
        questionTypeMap.put(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral(),
                QuestionTypeEnum.TrueFalse.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral(),
                QuestionTypeEnum.SingleChoice.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral(),
                QuestionTypeEnum.DragAndDrop.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleAnswerQuestion.getLiteral(),
                QuestionTypeEnum.MultipleChoice.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.HotSpotImageQuestion.getLiteral(),
                QuestionTypeEnum.MultipleChoiceImage.getLiteral());
    }

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Autowired
    AssessmentRestClient assessmentRestClient;

    @Autowired
    CollectionRestClient collectionRestClient;

    public CollectionDto getAssessment(String assessmentId) {
        String userToken = authenticationRestClient.generateAnonymousToken();
        AssessmentContentDto assessmentContentDto = assessmentRestClient.getAssessment(assessmentId, userToken);
        return convertGooruAssessmentToQuizzesFormat(assessmentContentDto);
    }

    public CollectionDto getCollection(String collectionId) {
        String userToken = authenticationRestClient.generateAnonymousToken();
        CollectionContentDto collectionContentDto = collectionRestClient.getCollection(collectionId, userToken);
        return convertGooruCollectionToQuizzesFormat(collectionContentDto);
    }

    private CollectionDto convertGooruAssessmentToQuizzesFormat(AssessmentContentDto assessmentDto) {
        CollectionDto collectionDto = createCollectionDto(assessmentDto.getId(), assessmentDto.getTitle());
        collectionDto.setResources(mapResources(assessmentDto.getQuestions()));

        return collectionDto;
    }

    private CollectionDto convertGooruCollectionToQuizzesFormat(CollectionContentDto collectionContentDto) {
        CollectionDto collectionDto = createCollectionDto(collectionContentDto.getId(), collectionContentDto.getTitle());
        collectionDto.setResources(mapResources(collectionContentDto.getContent()));

        return collectionDto;
    }

    private CollectionDto createCollectionDto(String id, String title) {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(id);
        collectionDto.setMetadata(new CollectionMetadataDto(title));
        return collectionDto;
    }

    private List<ResourceDto> mapResources(List<ResourceContentDto> resourceContentDtos) {
        List<ResourceDto> resourceDtos = new ArrayList<>();

        if (resourceContentDtos != null) {
            resourceDtos = resourceContentDtos.stream().map(resourceContentDto -> {
                ResourceDto resourceDto = new ResourceDto();
                resourceDto.setId(UUID.fromString(resourceContentDto.getId()));
                resourceDto.setSequence((short) resourceContentDto.getSequence());

                ResourceMetadataDto metadata;
                boolean isResource = false;
                if (resourceContentDto.getContentFormat() == null ||
                        !resourceContentDto.getContentFormat().equals("resource")) {
                    metadata = mapQuestionResource(resourceContentDto);
                } else {
                    metadata = mapResource(resourceContentDto);
                    isResource = true;
                }

                resourceDto.setIsResource(isResource);
                resourceDto.setMetadata(metadata);

                return resourceDto;
            }).collect(Collectors.toList());
        }

        return resourceDtos;
    }

    private ResourceMetadataDto mapResource(ResourceContentDto resourceContentDto) {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceContentDto.getTitle());
        metadata.setType(resourceContentDto.getContentSubformat());
        metadata.setUrl(resourceContentDto.getUrl());
        return metadata;
    }

    private ResourceMetadataDto mapQuestionResource(ResourceContentDto resourceContentDto) {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceContentDto.getTitle());
        metadata.setType(mapQuestionType(resourceContentDto.getContentSubformat()));
        if(resourceContentDto.getAnswers() != null){
            metadata.setCorrectAnswer(getCorrectAnswers(resourceContentDto.getAnswers()));
            metadata.setInteraction(createInteraction(resourceContentDto.getAnswers()));
        }
        metadata.setBody(resourceContentDto.getTitle());
        return metadata;
    }

    private String mapQuestionType(String gooruQuestionType) {
        String mappedType = questionTypeMap.get(gooruQuestionType);
        if (mappedType == null) {
            mappedType = QuestionTypeEnum.None.getLiteral();
        }
        return mappedType;
    }

    private List<AnswerDto> getCorrectAnswers(List<AnswerContentDto> answers) {
        List<AnswerDto> correctAnswers = new ArrayList<>();
        if (answers != null) {
            correctAnswers = answers.stream()
                    .filter(answer -> answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1"))
                    .map(answer -> new AnswerDto(encodeAnswer(answer.getAnswerText())))
                    .collect(Collectors.toList());
        }
        return correctAnswers;
    }

    private InteractionDto createInteraction(List<AnswerContentDto> answers) {
        List<ChoiceDto> choices = answers.stream().map(answer -> {
            ChoiceDto choiceDto = new ChoiceDto();
            choiceDto.setFixed(true);
            choiceDto.setText(answer.getAnswerText());
            choiceDto.setValue(encodeAnswer(answer.getAnswerText()));
            choiceDto.setSequence(answer.getSequence());
            return choiceDto;
        }).collect(Collectors.toList());

        InteractionDto interactionDto = new InteractionDto();
        interactionDto.setShuffle(false);
        interactionDto.setMaxChoices(0);
        interactionDto.setPrompt("");
        interactionDto.setChoices(choices);

        return interactionDto;
    }

    private String encodeAnswer(String answer) {
        byte[] message = answer.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(message);
    }

}
