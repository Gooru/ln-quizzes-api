package com.quizzes.api.core.services.content;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.code.ssm.api.ReturnDataUpdateContent;
import com.google.code.ssm.api.UpdateSingleCache;
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
import com.quizzes.api.core.exceptions.ContentNotFoundException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private static final Map<String, String> questionTypeMap;
    private static final Pattern hotTextHighlightPattern = Pattern.compile("\\[(.*?)\\]");

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
        questionTypeMap.put(GooruQuestionTypeEnum.HotSpotTextQuestion.getLiteral(),
                QuestionTypeEnum.MultipleChoiceText.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.WordHotTextHighlightQuestion.getLiteral(),
                QuestionTypeEnum.HotTextWord.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.SentenceHotTextHighlightQuestion.getLiteral(),
                QuestionTypeEnum.HotTextSentence.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral(),
                QuestionTypeEnum.TextEntry.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral(),
                QuestionTypeEnum.ExtendedText.getLiteral());
    }

    @Autowired
    private AuthenticationRestClient authenticationRestClient;

    @Autowired
    private AssessmentRestClient assessmentRestClient;

    @Autowired
    private CollectionRestClient collectionRestClient;

    @ReadThroughSingleCache(namespace = "Assessments")
    public CollectionDto getAssessment(@ParameterValueKeyProvider UUID assessmentId) {
        String token = authenticationRestClient.generateAnonymousToken();
        AssessmentContentDto assessmentContentDto = assessmentRestClient.getAssessment(assessmentId, token);
        return createCollectionDtoFromAssessmentContentDto(assessmentContentDto);
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Assessments")
    public CollectionDto getAssessmentWithCacheRefresh(@ParameterValueKeyProvider UUID assessmentId) {
        return getAssessment(assessmentId);
    }

    @ReadThroughSingleCache(namespace = "Collections")
    public CollectionDto getCollection(@ParameterValueKeyProvider UUID collectionId) {
        String token = authenticationRestClient.generateAnonymousToken();
        CollectionContentDto collectionContentDto = collectionRestClient.getCollection(collectionId, token);
        return createCollectionDtoFromCollectionContentDto(collectionContentDto);
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

    private CollectionDto createCollectionDtoFromCollectionContentDto(CollectionContentDto collectionContentDto) {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionContentDto.getId());
        collectionDto.setOwnerId(collectionContentDto.getOwnerId());
        collectionDto.setMetadata(mapCollectionMetadata(collectionContentDto));
        collectionDto.setResources(mapResources(collectionContentDto.getContent()));
        collectionDto.setIsCollection(collectionContentDto.getIsCollection());
        return collectionDto;
    }

    private CollectionMetadataDto mapCollectionMetadata(CollectionContentDto collectionContentDto) {
        CollectionMetadataDto collectionMetadataDto = new CollectionMetadataDto();
        collectionMetadataDto.setTitle(collectionContentDto.getTitle());
        collectionMetadataDto.setSetting(collectionContentDto.getSetting());
        collectionMetadataDto.setTaxonomy(collectionContentDto.getTaxonomy());
        return collectionMetadataDto;
    }

    private CollectionDto createCollectionDtoFromAssessmentContentDto(AssessmentContentDto assessmentContentDto) {
        CollectionDto collectionDto = createCollectionDtoFromCollectionContentDto(assessmentContentDto);
        collectionDto.setResources(mapResources(assessmentContentDto.getQuestions()));
        return collectionDto;
    }

    private List<ResourceDto> mapResources(List<ResourceContentDto> resourceContentDtos) {
        List<ResourceDto> resourceDtos = new ArrayList<>();

        if (resourceContentDtos != null) {
            resourceDtos = resourceContentDtos.stream().map(resourceContentDto -> {
                ResourceDto resourceDto = new ResourceDto();
                resourceDto.setId(resourceContentDto.getId());
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
        metadata.setTaxonomy(resourceContentDto.getTaxonomy());
        return metadata;
    }

    private ResourceMetadataDto mapQuestionResource(ResourceContentDto resourceContentDto) {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceContentDto.getTitle());
        metadata.setType(mapQuestionType(resourceContentDto));
        if(resourceContentDto.getAnswers() != null){
            metadata.setCorrectAnswer(getCorrectAnswers(resourceContentDto));
            metadata.setInteraction(createInteraction(resourceContentDto));
        }
        metadata.setBody(getBody(resourceContentDto));
        metadata.setTaxonomy(resourceContentDto.getTaxonomy());
        return metadata;
    }

    private String mapQuestionType(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        if (resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral())) {
            contentSubformat = resourceContentDto.getAnswers().get(0).getHighlightType() + "_" +
                    contentSubformat;
        }

        String mappedType = questionTypeMap.get(contentSubformat);
        return (mappedType == null) ? QuestionTypeEnum.None.getLiteral() : mappedType;
    }

    private List<AnswerDto> getCorrectAnswers(ResourceContentDto resourceContentDto) {
        if (resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral())) {
            return getHotTextHighlightCorrectAnswers(resourceContentDto);
        }
        if (resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral())) {
            return getMultipleChoiceCorrectAnswers(resourceContentDto.getAnswers(), false);
        }
        if (resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral())) {
            return null;
        }
        return getMultipleChoiceCorrectAnswers(resourceContentDto.getAnswers(), true);
    }

    private List<AnswerDto> getHotTextHighlightCorrectAnswers(ResourceContentDto resourceContentDto) {
        List<AnswerDto> correctAnswers = new ArrayList<>();
        String answerText = resourceContentDto.getAnswers().get(0).getAnswerText();
        Matcher hotTextHighlightMatcher = hotTextHighlightPattern.matcher(answerText);

        int answerCount = 0;
        while (hotTextHighlightMatcher.find()) {
            int answerStart = hotTextHighlightMatcher.start(1) - (answerCount * 2) - 1; // matcher start - (2x + 1) to counter the missing [] on the FE
            String answerValue = answerText.substring(hotTextHighlightMatcher.start(1), hotTextHighlightMatcher.end(1));
            correctAnswers.add(new AnswerDto(answerValue + "," + answerStart));
            answerCount++;
        }
        return correctAnswers;
    }

    private List<AnswerDto> getMultipleChoiceCorrectAnswers(List<AnswerContentDto> answers, boolean encodeValues) {
        List<AnswerDto> correctAnswers = new ArrayList<>();
        if (answers != null) {
            correctAnswers = answers.stream()
                    .filter(answer -> answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1"))
                    .map(answer -> new AnswerDto(encodeValues ? encodeAnswer(answer.getAnswerText()) : answer.getAnswerText()))
                    .collect(Collectors.toList());
        }
        return correctAnswers;
    }

    private String getBody(ResourceContentDto resource) {
        if (resource.getContentSubformat().equals(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral())) {
            return resource.getAnswers().get(0).getAnswerText().replaceAll("(\\[|\\])", "");
        }
        if (resource.getContentSubformat().equals(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral())) {
            return resource.getDescription().replaceAll("(?<=\\[)(.*?)(?=\\])", "");
        }
        if (resource.getContentSubformat().equals(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral())) {
            return resource.getDescription();
        }
        return resource.getTitle();
    }

    private InteractionDto createInteraction(ResourceContentDto resourceContentDto) {
        if (resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral()) ||
                resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral()) ||
                resourceContentDto.getContentSubformat().equals(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral())) {
            return null;
        }

        List<ChoiceDto> choices = resourceContentDto.getAnswers().stream().map(answer -> {
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
