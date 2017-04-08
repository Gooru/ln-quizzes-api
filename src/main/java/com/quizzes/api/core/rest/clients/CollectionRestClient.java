package com.quizzes.api.core.rest.clients;

import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.code.ssm.api.ReturnDataUpdateContent;
import com.google.code.ssm.api.UpdateSingleCache;
import com.google.gson.Gson;
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
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import com.quizzes.api.util.QuizzesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CollectionRestClient {

    private static final String NUCLEUS_API_URL = "/api/nucleus/v1";
    private static final String ASSESSMENTS_PATH = NUCLEUS_API_URL.concat("/assessments/%s");
    private static final String COLLECTIONS_PATH = NUCLEUS_API_URL.concat("/collections/%s");

    private final Pattern hotTextHighlightPattern = Pattern.compile("\\[(.*?)\\]");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Map<GooruQuestionTypeEnum, QuestionTypeEnum> questionTypeMap;

    static {
        questionTypeMap = new HashMap<>();
        questionTypeMap.put(GooruQuestionTypeEnum.TrueFalseQuestion, QuestionTypeEnum.TrueFalse);
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleChoiceQuestion, QuestionTypeEnum.SingleChoice);
        questionTypeMap.put(GooruQuestionTypeEnum.HotTextReorderQuestion, QuestionTypeEnum.DragAndDrop);
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleAnswerQuestion, QuestionTypeEnum.MultipleChoice);
        questionTypeMap.put(GooruQuestionTypeEnum.HotSpotImageQuestion, QuestionTypeEnum.MultipleChoiceImage);
        questionTypeMap.put(GooruQuestionTypeEnum.HotSpotTextQuestion, QuestionTypeEnum.MultipleChoiceText);
        questionTypeMap.put(GooruQuestionTypeEnum.WordHotTextHighlightQuestion, QuestionTypeEnum.HotTextWord);
        questionTypeMap.put(GooruQuestionTypeEnum.SentenceHotTextHighlightQuestion, QuestionTypeEnum.HotTextSentence);
        questionTypeMap.put(GooruQuestionTypeEnum.FillInTheBlankQuestion, QuestionTypeEnum.TextEntry);
        questionTypeMap.put(GooruQuestionTypeEnum.OpenEndedQuestion, QuestionTypeEnum.ExtendedText);
        questionTypeMap.put(GooruQuestionTypeEnum.Unknown, QuestionTypeEnum.Unknown);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;

    @Autowired
    private AuthenticationRestClient authenticationRestClient;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private ConfigurationService configurationService;

    @ReadThroughSingleCache(namespace = "Collections")
    public CollectionDto getCollection(@ParameterValueKeyProvider UUID collectionId, String authToken) {
        String endpointUrl = configurationService.getContentApiUrl() + String.format(COLLECTIONS_PATH, collectionId);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(authToken);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<CollectionContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, CollectionContentDto.class);
            CollectionContentDto collection = responseEntity.getBody();
            collection.setIsCollection(true);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gson.toJson(collection));
            }

            return createCollectionDtoFromCollectionContentDto(collection);
        } catch (HttpClientErrorException hcee) {
            logger.error("Gooru Collection '" + collectionId + "' could not be retrieved.", hcee);
            if (hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ContentNotFoundException("Collection " + collectionId + " could not be found.");
            }
            throw new ContentProviderException("Collection " + collectionId + " could not be retrieved.", hcee);
        } catch (Exception e) {
            logger.error("Gooru Collection '" + collectionId + "' could not be processed.", e);
            throw new InternalServerException("Collection " + collectionId + " could not be processed.", e);
        }
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Collections")
    public CollectionDto getCollectionWithCacheRefresh(@ParameterValueKeyProvider UUID collectionId, String authToken) {
        return getCollection(collectionId, authToken);
    }

    @ReadThroughSingleCache(namespace = "Assessments")
    public CollectionDto getAssessment(@ParameterValueKeyProvider UUID assessmentId, String authToken) {
        String endpointUrl = configurationService.getContentApiUrl() + String.format(ASSESSMENTS_PATH, assessmentId);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(authToken);
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<AssessmentContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, requestEntity, AssessmentContentDto.class);
            AssessmentContentDto assessment = responseEntity.getBody();
            assessment.setIsCollection(false);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gson.toJson(assessment));
            }

            return createCollectionDtoFromAssessmentContentDto(assessment);
        } catch (HttpClientErrorException hcee) {
            if (hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("Assessment " + assessmentId + " not found in Gooru.", hcee);
                throw new ContentNotFoundException("Assessment " + assessmentId + " not found in Gooru.");
            } else {
                logger.error("Assessment " + assessmentId + " could not be retrieved.", hcee);
                throw new ContentProviderException("Assessment " + assessmentId + " could not be retrieved.", hcee);
            }
        } catch (Exception e) {
            logger.error("Getting Assessment " + assessmentId + " process failed.", e);
            throw new InternalServerException("Getting Assessment " + assessmentId + " process failed.", e);
        }
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Assessments")
    public CollectionDto getAssessmentWithCacheRefresh(@ParameterValueKeyProvider UUID assessmentId, String authToken) {
        return getAssessment(assessmentId, authToken);
    }

    private CollectionDto createCollectionDtoFromCollectionContentDto(CollectionContentDto collectionContentDto) {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionContentDto.getId());
        collectionDto.setOwnerId(collectionContentDto.getOwnerId());
        collectionDto.setMetadata(mapCollectionMetadata(collectionContentDto));
        collectionDto.setResources(mapResources(collectionContentDto.getContent()));
        collectionDto.setIsCollection(collectionContentDto.getIsCollection());
        collectionDto.setUnitId(collectionContentDto.getUnitId());
        collectionDto.setLessonId(collectionContentDto.getLessonId());
        collectionDto.setCourseId(collectionContentDto.getCourseId());
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
            resourceDtos = resourceContentDtos.stream()
                    .sorted(Comparator.comparingInt(ResourceContentDto::getSequence))
                    .map(resourceContentDto -> {
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
        metadata.setDescription(resourceContentDto.getDescription());
        metadata.setType(resourceContentDto.getContentSubformat());
        metadata.setUrl(resourceContentDto.getUrl());
        metadata.setTaxonomy(resourceContentDto.getTaxonomy());
        metadata.setDisplayGuide(resourceContentDto.getDisplayGuide());
        return metadata;
    }

    private ResourceMetadataDto mapQuestionResource(ResourceContentDto resourceContentDto) {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceContentDto.getTitle());
        metadata.setDescription(getDescription(resourceContentDto));
        metadata.setType(mapQuestionType(resourceContentDto).getLiteral());
        metadata.setThumbnail(resourceContentDto.getThumbnail());
        if (resourceContentDto.getAnswers() != null) {
            metadata.setCorrectAnswer(getCorrectAnswers(resourceContentDto));
            metadata.setInteraction(createInteraction(resourceContentDto));
        }
        metadata.setBody(getBody(resourceContentDto));
        metadata.setTaxonomy(resourceContentDto.getTaxonomy());
        return metadata;
    }

    private QuestionTypeEnum mapQuestionType(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        GooruQuestionTypeEnum resourceQuestionType = GooruQuestionTypeEnum.getEnum(contentSubformat);
        if (resourceQuestionType.equals(GooruQuestionTypeEnum.HotTextHighlightQuestion)) {
            String highlightType = resourceContentDto.getAnswers().get(0).getHighlightType();
            resourceQuestionType = GooruQuestionTypeEnum.getEnum(highlightType + "_" + contentSubformat);
        }
        return questionTypeMap.get(resourceQuestionType);
    }

    private List<AnswerDto> getCorrectAnswers(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        GooruQuestionTypeEnum resourceQuestionType = GooruQuestionTypeEnum.getEnum(contentSubformat);
        if (resourceQuestionType.equals(GooruQuestionTypeEnum.OpenEndedQuestion)) {
            return null;
        } else if (resourceQuestionType.equals(GooruQuestionTypeEnum.HotTextHighlightQuestion)) {
            return getHotTextHighlightCorrectAnswers(resourceContentDto);
        } else if (resourceQuestionType.equals(GooruQuestionTypeEnum.MultipleAnswerQuestion)) {
            return getMultipleAnswerCorrectAnswers(resourceContentDto.getAnswers());
        } else if (resourceQuestionType.equals(GooruQuestionTypeEnum.FillInTheBlankQuestion)) {
            return getMultipleChoiceCorrectAnswers(resourceContentDto.getAnswers(), false);
        } else {
            return getMultipleChoiceCorrectAnswers(resourceContentDto.getAnswers(), true);
        }
    }

    private List<AnswerDto> getHotTextHighlightCorrectAnswers(ResourceContentDto resourceContentDto) {
        List<AnswerDto> correctAnswers = new ArrayList<>();
        String answerText = resourceContentDto.getAnswers().get(0).getAnswerText();
        Matcher hotTextHighlightMatcher = hotTextHighlightPattern.matcher(answerText);

        int answerCount = 0;
        while (hotTextHighlightMatcher.find()) {
            // matcher start - (2x + 1) to counter the missing [] on the FE
            int answerStart = hotTextHighlightMatcher.start(1) - (answerCount * 2) - 1;
            String answerValue = answerText.substring(hotTextHighlightMatcher.start(1), hotTextHighlightMatcher.end(1));
            correctAnswers.add(new AnswerDto(answerValue + "," + answerStart));
            answerCount++;
        }
        return correctAnswers;
    }

    private List<AnswerDto> getMultipleChoiceCorrectAnswers(List<AnswerContentDto> answers, boolean encodeAnswer) {
        final List<AnswerDto> correctAnswers = new ArrayList<>();
        if (answers != null) {
            answers.forEach(answer -> {
                if (answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1")) {
                    correctAnswers.add(new AnswerDto(encodeAnswer
                            ? QuizzesUtils.encodeString(answer.getAnswerText())
                            : answer.getAnswerText()));
                }
            });
        }
        return correctAnswers;
    }

    private List<AnswerDto> getMultipleAnswerCorrectAnswers(List<AnswerContentDto> answers) {
        final List<AnswerDto> correctAnswers = new ArrayList<>();
        if (answers != null) {
            answers.forEach(answer -> {
                if (answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1")) {
                    correctAnswers.add(new AnswerDto(QuizzesUtils.encodeString(String.valueOf(answer.getSequence()))));
                }
            });
        }
        return correctAnswers;
    }

    private String getBody(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        GooruQuestionTypeEnum resourceQuestionType = GooruQuestionTypeEnum.getEnum(contentSubformat);
        if (resourceQuestionType.equals(GooruQuestionTypeEnum.HotTextHighlightQuestion)) {
            return resourceContentDto.getAnswers().get(0).getAnswerText().replaceAll("(\\[|\\])", "");
        } else {
            return getDescription(resourceContentDto);
        }
    }

    private String getDescription(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        GooruQuestionTypeEnum resourceQuestionType = GooruQuestionTypeEnum.getEnum(contentSubformat);
        if (resourceQuestionType.equals(GooruQuestionTypeEnum.FillInTheBlankQuestion)) {
            return resourceContentDto.getDescription().replaceAll("(?<=\\[)(.*?)(?=\\])", "");
        } else {
            return resourceContentDto.getDescription();
        }
    }

    private InteractionDto createInteraction(ResourceContentDto resourceContentDto) {
        String contentSubformat = resourceContentDto.getContentSubformat();
        GooruQuestionTypeEnum resourceQuestionType = GooruQuestionTypeEnum.getEnum(contentSubformat);
        if (resourceQuestionType.equals(GooruQuestionTypeEnum.HotTextHighlightQuestion) ||
                resourceQuestionType.equals(GooruQuestionTypeEnum.FillInTheBlankQuestion) ||
                resourceQuestionType.equals(GooruQuestionTypeEnum.OpenEndedQuestion)) {
            return null;
        }

        List<ChoiceDto> choices = resourceContentDto.getAnswers().stream().map(answer -> {
            ChoiceDto choiceDto = new ChoiceDto();
            choiceDto.setIsFixed(true);
            choiceDto.setText(answer.getAnswerText());
            if (resourceQuestionType.equals(GooruQuestionTypeEnum.MultipleAnswerQuestion)) {
                // Sets the value using the encoded Sequence
                choiceDto.setValue(QuizzesUtils.encodeString(String.valueOf(answer.getSequence())));
            } else {
                // Sets the value using the encoded AnswerText
                choiceDto.setValue(QuizzesUtils.encodeString(answer.getAnswerText()));
            }
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

}