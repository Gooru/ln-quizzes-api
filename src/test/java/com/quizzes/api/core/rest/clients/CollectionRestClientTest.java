package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.InteractionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import com.quizzes.api.util.QuizzesUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CollectionRestClient.class})
public class CollectionRestClientTest {

    @InjectMocks
    private CollectionRestClient collectionRestClient = spy(new CollectionRestClient());

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private GooruHelper gooruHelper;

    private UUID collectionId;
    private UUID assessmentId;
    private UUID resourceId;
    private UUID questionId;
    private UUID ownerProfileId;
    private String url;
    private String token;
    private String collectionTitle;
    private String assessmentTitle;
    private String resourceTitle;
    private String questionTitle;
    private String resourceDescription;
    private String trueFalseQuestion;
    private String imageResource;
    private String thumbnail;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;
    private Map<String, Object> displayGuide;

    @Before
    public void before() throws Exception {
        collectionId = UUID.randomUUID();
        assessmentId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        questionId = UUID.randomUUID();
        ownerProfileId = UUID.randomUUID();
        url = "http://www.gooru.org";
        token = "token-id";
        collectionTitle = "Collection Title";
        assessmentTitle = "Assessment Title";
        resourceTitle = "Resource title";
        questionTitle = "Question title";
        resourceDescription = "Question description";
        trueFalseQuestion = "true_false_question";
        imageResource = "www.image.com";
        thumbnail = "image.jpg";
        setting = new HashMap<>();
        setting.put("key", "value");
        taxonomy = new HashMap<>();
        taxonomy.put("A", new Object());
        displayGuide = new HashMap<>();
        displayGuide.put("key", "value");
    }

    @Test
    public void getCollection() throws Exception {
        CollectionContentDto collectionContentDto = createCollectionContentDtoForCollection();

        doReturn(token).when(authenticationRestClient).generateAnonymousToken();
        doReturn(url).when(configurationService).getContentApiUrl();
        doReturn(new HttpHeaders()).when(gooruHelper).setupHttpHeaders(anyString());
        doReturn(new ResponseEntity<>(collectionContentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CollectionContentDto.class));

        CollectionDto result = collectionRestClient.getCollection(collectionId, "token");

        verify(configurationService, times(1)).getContentApiUrl();
        verify(gooruHelper, times(1)).setupHttpHeaders(anyString());
        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CollectionContentDto.class));
        verifyPrivate(collectionRestClient, times(1))
                .invoke("createCollectionDtoFromCollectionContentDto", any(CollectionContentDto.class));

        assertEquals("Wrong Collection ID", collectionId.toString(), result.getId());
        assertTrue("IsCollection value is wrong", result.getIsCollection());
        assertEquals("Wrong number of resources", 1, result.getResources().size());
        assertNotNull("Wrong collection title", result.getMetadata().getTitle());
        assertNotNull("Setting is null", result.getMetadata().getSetting());
        assertNotNull("Taxonomy is null", result.getMetadata().getTaxonomy());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong Resource ID", resourceId, resourceResult.getId());
        assertTrue("IsResource value is wrong", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertNull("Body is not null", metadataResource.getBody());
        assertEquals("Wrong Resource type", imageResource, metadataResource.getType());
        assertNull("Thumbnail is not null", metadataResource.getThumbnail());
        assertEquals("Wrong Resource url", url, metadataResource.getUrl());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
        assertNotNull("Taxonomy is not null", metadataResource.getTaxonomy());
    }

    @Test
    public void getAssessment() throws Exception {
        AssessmentContentDto assessmentContentDto = createCollectionDtoForAssessment();

        doReturn(token).when(authenticationRestClient).generateAnonymousToken();
        doReturn(url).when(configurationService).getContentApiUrl();
        doReturn(new HttpHeaders()).when(gooruHelper).setupHttpHeaders(anyString());
        doReturn(new ResponseEntity<>(assessmentContentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));

        CollectionDto result = collectionRestClient.getAssessment(collectionId, "token");

        verify(configurationService, times(1)).getContentApiUrl();
        verify(gooruHelper, times(1)).setupHttpHeaders(anyString());
        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));
        verifyPrivate(collectionRestClient, times(1))
                .invoke("createCollectionDtoFromAssessmentContentDto", any(AssessmentContentDto.class));

        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertEquals("Wrong Assessment title", assessmentTitle, result.getMetadata().getTitle());
        assertNotNull("Setting is null", result.getMetadata().getSetting());
        assertNotNull("Taxonomy is null", result.getMetadata().getTaxonomy());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong Question ID", questionId, resourceResult.getId());
        assertFalse("IsResource value is wrong", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong Question title", questionTitle, metadataResource.getTitle());
        assertEquals("Wrong description", resourceDescription, metadataResource.getDescription());
        assertEquals("Wrong Question type", QuestionTypeEnum.TrueFalse.getLiteral(), metadataResource.getType());
        assertEquals("Wrong body text", resourceDescription, metadataResource.getBody());
        assertEquals("Wrong thumbnail", thumbnail, metadataResource.getThumbnail());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResource.getUrl());
        assertNotNull("Taxonomy is null", metadataResource.getTaxonomy());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 1, interactionResult.getChoices().size());
    }

    @Test
    public void convertGooruAssessmentToQuizzesFormat() throws Exception {
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();
        assessmentContentDto.setId(assessmentId.toString());
        assessmentContentDto.setTitle(collectionTitle);
        assessmentContentDto.setQuestions(Arrays.asList(createQuestionContentDto()));
        assessmentContentDto.setSetting(setting);
        assessmentContentDto.setTaxonomy(taxonomy);

        doReturn(Arrays.asList(createResourceDto(resourceId, false, 1, createResourceMetadataDtoForQuestion())))
                .when(collectionRestClient, "mapResources", assessmentContentDto.getQuestions());

        CollectionDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "createCollectionDtoFromAssessmentContentDto",
                        assessmentContentDto);

        verifyPrivate(collectionRestClient, times(1)).invoke("mapResources", assessmentContentDto.getQuestions());

        assertEquals("Wrong assessment ID", assessmentId.toString(), result.getId());
        assertEquals("Wrong assessment title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong assessment setting", setting, result.getMetadata().getSetting());
        assertEquals("Wrong assessment taxonomy", taxonomy, result.getMetadata().getTaxonomy());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", questionTitle, metadataResource.getTitle());
        assertEquals("Wrong body", resourceDescription, metadataResource.getBody());
        assertEquals("Wrong description", resourceDescription, metadataResource.getDescription());
        assertEquals("Wrong thumbnail", thumbnail, metadataResource.getThumbnail());
        assertEquals("Wrong question type", trueFalseQuestion, metadataResource.getType());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResource.getUrl());
        assertEquals("Wrong assessment taxonomy", taxonomy, metadataResource.getTaxonomy());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void convertGooruCollectionToQuizzesFormat() throws Exception {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());
        collectionContentDto.setTitle(collectionTitle);
        collectionContentDto.setContent(Arrays.asList(createResourceContentDto()));
        collectionContentDto.setTaxonomy(taxonomy);
        collectionContentDto.setSetting(setting);

        doReturn(Arrays.asList(createResourceDto(resourceId, false, 1, createResourceMetadataDtoForResource())))
                .when(collectionRestClient, "mapResources", collectionContentDto.getContent());

        CollectionDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "createCollectionDtoFromCollectionContentDto",
                        collectionContentDto);

        verifyPrivate(collectionRestClient, times(1)).invoke("mapResources", collectionContentDto.getContent());

        assertEquals("Wrong collection ID", collectionId.toString(), result.getId());
        assertEquals("Wrong collection title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong collection setting", setting, result.getMetadata().getSetting());
        assertEquals("Wrong collection taxonomy", taxonomy, result.getMetadata().getTaxonomy());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong collection ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong resource type", imageResource, metadataResource.getType());
        assertEquals("Wrong thumbnail", thumbnail, metadataResource.getThumbnail());
        assertEquals("Wrong url", url, metadataResource.getUrl());
        assertNull("Body is not null", metadataResource.getBody());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
        assertEquals("Wrong resource taxonomy", taxonomy, metadataResource.getTaxonomy());
        assertEquals("Wrong resource display guide", displayGuide, metadataResource.getDisplayGuide());
    }

    @Test
    public void mapResourcesTypeResource() throws Exception {
        List<ResourceContentDto> questions = Arrays.asList(createResourceContentDto());

        doReturn(createResourceMetadataDtoForResource()).when(collectionRestClient, "mapResource",
                any(ResourceContentDto.class));
        doReturn(null).when(collectionRestClient, "mapQuestionResource",
                any(ResourceContentDto.class));

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(collectionRestClient, "mapResources", questions);

        verifyPrivate(collectionRestClient, times(1)).invoke("mapResource", any(ResourceContentDto.class));
        verifyPrivate(collectionRestClient, times(0)).invoke("mapQuestionResource", any(ResourceContentDto.class));

        assertEquals("Wrong number of resources", 1, result.size());

        ResourceDto resourceResult = result.get(0);
        assertEquals("Wrong resource ID", resourceId, resourceResult.getId());
        assertTrue("IsResource is false ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertNull("Body is not null", metadataResource.getBody());
        assertEquals("Wrong resource type", imageResource, metadataResource.getType());
        assertEquals("Wrong thumbnail", thumbnail, metadataResource.getThumbnail());
        assertEquals("Wrong url", url, metadataResource.getUrl());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
        assertEquals("Wrong taxonomy", taxonomy, metadataResource.getTaxonomy());
        assertEquals("Wrong display guide", displayGuide, metadataResource.getDisplayGuide());
    }

    @Test
    public void mapResourcesTypeQuestion() throws Exception {
        List<ResourceContentDto> questions = Arrays.asList(createQuestionContentDto());

        doReturn(null).when(collectionRestClient, "mapResource",
                any(ResourceContentDto.class));
        doReturn(createResourceMetadataDtoForQuestion()).when(collectionRestClient, "mapQuestionResource",
                any(ResourceContentDto.class));

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(collectionRestClient, "mapResources", questions);

        verifyPrivate(collectionRestClient, times(0)).invoke("mapResource", any(ResourceContentDto.class));
        verifyPrivate(collectionRestClient, times(1)).invoke("mapQuestionResource", any(ResourceContentDto.class));

        assertEquals("Wrong number of resources", 1, result.size());

        ResourceDto resourceResult = result.get(0);
        assertEquals("Wrong resource ID", questionId, resourceResult.getId());
        assertFalse("Wrong isResource is true", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResult = resourceResult.getMetadata();
        assertEquals("Wrong title", questionTitle, metadataResult.getTitle());
        assertEquals("Wrong description", resourceDescription, metadataResult.getDescription());
        assertEquals("Wrong body", resourceDescription, metadataResult.getBody());
        assertEquals("Wrong thumbnail", thumbnail, metadataResult.getThumbnail());
        assertEquals("Wrong type", trueFalseQuestion, metadataResult.getType());
        assertEquals("Wrong answer size", 1, metadataResult.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResult.getUrl());

        InteractionDto interactionResult = metadataResult.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
        assertEquals("Wrong taxonomy", taxonomy, metadataResult.getTaxonomy());
    }

    @Test
    public void mapResource() throws Exception {
        ResourceContentDto resourceContentDto = createResourceContentDto();

        ResourceMetadataDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "mapResource", resourceContentDto);

        assertEquals("Wrong title", resourceTitle, result.getTitle());
        assertEquals("Wrong resource type", imageResource, result.getType());
        assertNull("Thumbnail is not null", result.getThumbnail());
        assertEquals("Wrong url", url, result.getUrl());
        assertNull("Body is not null", result.getBody());
        assertNull("Answer is not null", result.getCorrectAnswer());
        assertNull("Interaction is not null", result.getInteraction());
        assertEquals("Wrong taxonomy", taxonomy, result.getTaxonomy());
    }

    @Test
    public void mapQuestionResource() throws Exception {
        ResourceContentDto resourceContentDto = createQuestionContentDto();

        doReturn(QuestionTypeEnum.TrueFalse).when(collectionRestClient, "mapQuestionType", resourceContentDto);
        doReturn(Arrays.asList(new AnswerDto("A"))).when(collectionRestClient, "getCorrectAnswers",
                resourceContentDto);
        doReturn(createInteractionDto()).when(collectionRestClient, "createInteraction",
                resourceContentDto);

        ResourceMetadataDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionResource", resourceContentDto);

        verifyPrivate(collectionRestClient, times(1)).invoke("mapQuestionType", resourceContentDto);
        verifyPrivate(collectionRestClient, times(1)).invoke("getCorrectAnswers", resourceContentDto);
        verifyPrivate(collectionRestClient, times(1)).invoke("createInteraction", resourceContentDto);

        assertEquals("Wrong title", questionTitle, result.getTitle());
        assertEquals("Wrong description", resourceDescription, result.getDescription());
        assertEquals("Wrong body", resourceDescription, result.getBody());
        assertEquals("Wrong thumbnail", thumbnail, result.getThumbnail());
        assertEquals("Wrong type", QuestionTypeEnum.TrueFalse.getLiteral(), result.getType());
        assertEquals("Wrong type", 1, result.getCorrectAnswer().size());
        assertNull("Url is not null", result.getUrl());
        assertEquals("Wrong taxonomy", taxonomy, result.getTaxonomy());

        InteractionDto interactionResult = result.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void getBody() throws Exception {
        ResourceContentDto resourceContentDto = createQuestionContentDto();

        String result = WhiteboxImpl.invokeMethod(collectionRestClient, "getBody", resourceContentDto);

        assertEquals("Wrong body content", resourceContentDto.getDescription(), result);
    }

    @Test
    public void getBodyHotTextHighlight() throws Exception {
        ResourceContentDto resourceContentDto = createHotTextHighlightResourceContentDto("word");

        String result = WhiteboxImpl.invokeMethod(collectionRestClient, "getBody", resourceContentDto);

        assertEquals("Wrong body content", "The big bad wolf blew down the house.", result);
    }

    @Test
    public void getBodyFillInTheBlank() throws Exception {
        ResourceContentDto resourceContentDto = createFillInTheBlankResourceContentDto();

        String result = WhiteboxImpl.invokeMethod(collectionRestClient, "getBody", resourceContentDto);

        assertEquals("Wrong body content", "(3x4) = []<br />(3x\\sqrt[3]{2}) = []", result);
    }

    @Test
    public void getBodyOpenEnded() throws Exception {
        ResourceContentDto resourceContentDto = createOpenEndedResourceContentDto();

        String result = WhiteboxImpl.invokeMethod(collectionRestClient, "getBody", resourceContentDto);

        assertEquals("Wrong body content", "Enter your thoughts here", result);
    }

    @Test
    public void createInteraction() throws Exception {
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "text");
        AnswerContentDto answer2 = createAnswerContentDto("2", "1", 2, "text");
        List<AnswerContentDto> answers = Arrays.asList(answer1, answer2);
        ResourceContentDto resourceContentDto = createQuestionContentDto();
        resourceContentDto.setAnswers(answers);

        InteractionDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "createInteraction", resourceContentDto);

        assertEquals("Wrong number of maxChoices", 0, result.getMaxChoices());
        assertEquals("Wrong prompt value", "", result.getPrompt());
        assertFalse("Shuffle is true", result.getIsShuffle());
        assertEquals("Wrong number of choices", 2, result.getChoices().size());

        ChoiceDto choiceResult = result.getChoices().get(0);
        assertNotNull("Choice Id is null", choiceResult.getValue());
        assertEquals("Wrong sequence", 1, choiceResult.getSequence());
        assertEquals("Wrong text", "text", choiceResult.getText());
        assertTrue("Shuffle is false", choiceResult.getIsFixed());
    }

    @Test
    public void createInteractionHotTextHighlight() throws Exception {
        createNullInteraction(createHotTextHighlightResourceContentDto("word"));
    }

    @Test
    public void createInteractionFillInTheBlank() throws Exception {
        createNullInteraction(createFillInTheBlankResourceContentDto());
    }

    @Test
    public void createInteractionOpenEnded() throws Exception {
        createNullInteraction(createOpenEndedResourceContentDto());
    }

    @Test
    public void getCorrectAnswersMultipleChoice() throws Exception {
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "text");
        AnswerContentDto answer2 = createAnswerContentDto("2", "false", 2, "text");
        List<AnswerContentDto> answers = Arrays.asList(answer1, answer2);
        ResourceContentDto resourceContentDto = createQuestionContentDto();
        resourceContentDto.setAnswers(answers);

        List<AnswerDto> result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "getCorrectAnswers", resourceContentDto);

        assertEquals("Wrong number of answers", 1, result.size());

        AnswerDto answerResult = result.get(0);
        assertNotNull("Wrong choice Id", answerResult.getValue());
    }

    @Test
    public void getCorrectAnswersHotTextHighlight() throws Exception {
        ResourceContentDto resourceContentDto = createHotTextHighlightResourceContentDto("word");

        List<AnswerDto> resultWord =
                WhiteboxImpl.invokeMethod(collectionRestClient, "getCorrectAnswers", resourceContentDto);

        assertEquals("Wrong number of answers", 2, resultWord.size());
        assertEquals("Wrong first correct answer value", "big,4", resultWord.get(0).getValue());
        assertEquals("Wrong second correct answer value", "down,22", resultWord.get(1).getValue());

        resourceContentDto = createHotTextHighlightResourceContentDto("sentence");

        List<AnswerDto> resultSentence =
                WhiteboxImpl.invokeMethod(collectionRestClient, "getCorrectAnswers", resourceContentDto);

        assertEquals("Wrong number of answers", 2, resultSentence.size());
        assertEquals("Wrong first correct answer value", "big bad wolf,4", resultSentence.get(0).getValue());
        assertEquals("Wrong second correct answer value", "down the house.,22", resultSentence.get(1).getValue());
    }

    @Test
    public void getCorrectAnswersFillInTheBlank() throws Exception {
        ResourceContentDto resourceContentDto = createFillInTheBlankResourceContentDto();

        List<AnswerDto> result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "getCorrectAnswers", resourceContentDto);

        assertEquals("Wrong number of answers", 2, result.size());
        assertEquals("Wrong first correct answer value", "12", result.get(0).getValue());
        assertEquals("Wrong second correct answer value", "6", result.get(1).getValue());
    }

    @Test
    public void getCorrectAnswersOpenEnded() throws Exception {
        ResourceContentDto resourceContentDto = createOpenEndedResourceContentDto();

        List<AnswerDto> result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "getCorrectAnswers", resourceContentDto);

        assertNull("Open ended question correct answers not null", result);
    }

    @Test
    public void mapQuestionType() throws Exception {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        QuestionTypeEnum trueFalseQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("True/False question type wrongly mapped", QuestionTypeEnum.TrueFalse, trueFalseQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        QuestionTypeEnum singleChoiceQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("SingleChoice question type wrongly mapped", QuestionTypeEnum.SingleChoice,
                singleChoiceQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral());
        QuestionTypeEnum dragAndDropQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("DragAndDrop question type wrongly mapped", QuestionTypeEnum.DragAndDrop, dragAndDropQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.MultipleAnswerQuestion.getLiteral());
        QuestionTypeEnum multipleChoiceQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("MultipleChoice question type wrongly mapped", QuestionTypeEnum.MultipleChoice,
                multipleChoiceQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.HotSpotImageQuestion.getLiteral());
        QuestionTypeEnum multipleChoiceImageQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient,
                "mapQuestionType", resourceContentDto);
        assertEquals("MultipleChoiceImage question type wrongly mapped", QuestionTypeEnum.MultipleChoiceImage,
                multipleChoiceImageQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.HotSpotTextQuestion.getLiteral());
        QuestionTypeEnum multipleChoiceTextQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient,
                "mapQuestionType", resourceContentDto);
        assertEquals("MultipleChoiceText question type wrongly mapped", QuestionTypeEnum.MultipleChoiceText,
                multipleChoiceTextQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral());
        AnswerContentDto answer = new AnswerContentDto();
        answer.setHighlightType("word");
        resourceContentDto.setAnswers(Arrays.asList(answer));
        QuestionTypeEnum hotTextWordQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("HotTextWord question type wrongly mapped", QuestionTypeEnum.HotTextWord, hotTextWordQuestionType);
        answer.setHighlightType("sentence");
        resourceContentDto.setAnswers(Arrays.asList(answer));
        QuestionTypeEnum hotTextSentenceQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient,
                "mapQuestionType", resourceContentDto);
        assertEquals("HotTextSentence question type wrongly mapped", QuestionTypeEnum.HotTextSentence,
                hotTextSentenceQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral());
        QuestionTypeEnum textEntryQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("TextEntry question type wrongly mapped", QuestionTypeEnum.TextEntry, textEntryQuestionType);

        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral());
        QuestionTypeEnum extendedTextQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("ExtendedText question type wrongly mapped", QuestionTypeEnum.ExtendedText,
                extendedTextQuestionType);

        resourceContentDto.setContentSubformat("unknown");
        QuestionTypeEnum unknownQuestionType = WhiteboxImpl.invokeMethod(collectionRestClient, "mapQuestionType",
                resourceContentDto);
        assertEquals("None question type wrongly mapped", QuestionTypeEnum.Unknown, unknownQuestionType);
    }

    private ResourceContentDto createQuestionContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setId(questionId);
        resourceContentDto.setTitle(questionTitle);
        resourceContentDto.setDescription(resourceDescription);
        resourceContentDto.setThumbnail(thumbnail);
        resourceContentDto.setSequence(1);
        resourceContentDto.setContentSubformat(trueFalseQuestion);
        resourceContentDto.setTaxonomy(taxonomy);
        AnswerContentDto answer = createAnswerContentDto("1", "1", 1, "text");
        resourceContentDto.setAnswers(Arrays.asList(answer));
        return resourceContentDto;
    }

    private ResourceContentDto createResourceContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setId(resourceId);
        resourceContentDto.setTitle(resourceTitle);
        resourceContentDto.setSequence(1);
        resourceContentDto.setContentSubformat(imageResource);
        resourceContentDto.setContentFormat("resource");
        resourceContentDto.setUrl(url);
        resourceContentDto.setTaxonomy(taxonomy);
        return resourceContentDto;
    }

    private ResourceContentDto createHotTextHighlightResourceContentDto(String highlightType) {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.HotTextHighlightQuestion.getLiteral());
        AnswerContentDto answer = highlightType.equals("word") ?
                createAnswerContentDto("1", "1", 1, "The [big] bad wolf blew [down] the house.", "word") :
                createAnswerContentDto("1", "1", 1, "The [big bad wolf] blew [down the house.]", "sentence");
        resourceContentDto.setAnswers(Arrays.asList(answer));

        return resourceContentDto;
    }

    private ResourceContentDto createFillInTheBlankResourceContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral());
        resourceContentDto.setDescription("(3x4) = [12]<br />(3x\\sqrt[3]{2}) = [6]");
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "12");
        AnswerContentDto answer2 = createAnswerContentDto("2", "1", 2, "6");
        resourceContentDto.setAnswers(Arrays.asList(answer1, answer2));

        return resourceContentDto;
    }

    private ResourceContentDto createOpenEndedResourceContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setContentSubformat(GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral());
        resourceContentDto.setDescription("Enter your thoughts here");
        AnswerContentDto answer = createAnswerContentDto("1", "1", 1, "My thoughts...");
        resourceContentDto.setAnswers(Arrays.asList(answer));
        resourceContentDto.setTaxonomy(taxonomy);

        return resourceContentDto;
    }

    private AnswerContentDto createAnswerContentDto(String id, String isCorrect, int sequence, String text) {
        return createAnswerContentDto(id, isCorrect, sequence, text, null);
    }

    private AnswerContentDto createAnswerContentDto(String id, String isCorrect, int sequence, String text, String highlightType) {
        AnswerContentDto answer = new AnswerContentDto();
        answer.setIsCorrect(isCorrect);
        answer.setSequence(sequence);
        answer.setAnswerText(text);
        answer.setHighlightType(highlightType);
        answer.setId(id);
        return answer;
    }

    private CollectionContentDto createCollectionContentDtoForCollection() {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());
        collectionContentDto.setIsCollection(true);
        collectionContentDto.setTitle(assessmentTitle);
        collectionContentDto.setOwnerId(ownerProfileId);
        List<ResourceContentDto> resourceContentDtos = new ArrayList<>();
        resourceContentDtos.add(createResourceContentDto());
        collectionContentDto.setContent(resourceContentDtos);
        collectionContentDto.setSetting(setting);
        collectionContentDto.setTaxonomy(taxonomy);
        return collectionContentDto;
    }

    private AssessmentContentDto createCollectionDtoForAssessment() {
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();
        assessmentContentDto.setId(assessmentId.toString());
        assessmentContentDto.setIsCollection(false);
        assessmentContentDto.setTitle(assessmentTitle);
        assessmentContentDto.setOwnerId(ownerProfileId);
        List<ResourceContentDto> resourceContentDtos = new ArrayList<>();
        resourceContentDtos.add(createQuestionContentDto());
        assessmentContentDto.setQuestions(resourceContentDtos);
        assessmentContentDto.setSetting(setting);
        assessmentContentDto.setTaxonomy(taxonomy);
        return assessmentContentDto;
    }

    private ResourceMetadataDto createResourceMetadataDtoForQuestion() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(questionTitle);
        metadata.setDescription(resourceDescription);
        metadata.setType(trueFalseQuestion);
        metadata.setCorrectAnswer(Arrays.asList(new AnswerDto("A")));
        metadata.setInteraction(createInteractionDto());
        metadata.setBody(resourceDescription);
        metadata.setThumbnail(thumbnail);
        metadata.setTaxonomy(taxonomy);
        return metadata;
    }

    private ResourceMetadataDto createResourceMetadataDtoForResource() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceTitle);
        metadata.setType(imageResource);
        metadata.setThumbnail(thumbnail);
        metadata.setUrl(url);
        metadata.setTaxonomy(taxonomy);
        metadata.setDisplayGuide(displayGuide);
        return metadata;
    }

    private InteractionDto createInteractionDto() {
        InteractionDto interactionDto = new InteractionDto();
        interactionDto.setPrompt("");
        interactionDto.setShuffle(false);
        interactionDto.setMaxChoices(0);
        interactionDto.setChoices(
                Arrays.asList(createChoiceDto(true, "text", "A", 1), createChoiceDto(true, "text2", "B", 2)));
        return interactionDto;
    }

    private ChoiceDto createChoiceDto(boolean fixed, String text, String value, int sequence) {
        ChoiceDto choiceDto = new ChoiceDto();
        choiceDto.setIsFixed(fixed);
        choiceDto.setText(text);
        choiceDto.setValue(value);
        choiceDto.setSequence(sequence);
        return choiceDto;
    }

    private ResourceDto createResourceDto(UUID id, boolean isResource, int sequence, ResourceMetadataDto metadata) {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(id);
        resourceDto.setIsResource(isResource);
        resourceDto.setSequence(sequence);
        resourceDto.setMetadata(metadata);
        return resourceDto;
    }

    private void createNullInteraction(ResourceContentDto resourceContentDto) throws Exception {
        InteractionDto result =
                WhiteboxImpl.invokeMethod(collectionRestClient, "createInteraction", resourceContentDto);
        assertNull("Interaction is null", result);
    }

}
