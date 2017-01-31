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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AssessmentService.class)
public class AssessmentServiceTest {

    @InjectMocks
    private AssessmentService assessmentService = spy(new AssessmentService());

    @Mock
    private AssessmentRestClient assessmentRestClient;

    @Mock
    private CollectionRestClient collectionRestClient;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    private String assessmentId;
    private String collectionId;
    private UUID resourceId;
    private String questionId;
    private String collectionTitle;
    private String resourceTitle;
    private String questionTitle;
    private String trueFalseQuestion;
    private String token;
    private String url;
    private String imageResource;

    @Before
    public void before() throws Exception {
        assessmentId = UUID.randomUUID().toString();
        collectionId = UUID.randomUUID().toString();
        resourceId = UUID.randomUUID();
        questionId = UUID.randomUUID().toString();
        collectionTitle = "Assessment title";
        resourceTitle = "Resource title";
        questionTitle = "Question title";
        trueFalseQuestion = "true_false";
        token = "token-id";
        url = "www.url.com";
        imageResource = "www.image.com";
    }

    @Test
    public void getAssessment() throws Exception {
        CollectionDto collectionDto = createCollectionDtoForAssessment();
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();

        when(authenticationRestClient.generateAnonymousToken()).thenReturn(token);
        when(assessmentRestClient.getAssessment(assessmentId, token)).thenReturn(assessmentContentDto);
        doReturn(collectionDto).when(assessmentService, "convertGooruAssessmentToQuizzesFormat", assessmentContentDto);

        CollectionDto result = assessmentService.getAssessment(assessmentId);

        verify(authenticationRestClient, times(1)).generateAnonymousToken();
        verify(assessmentRestClient, times(1)).getAssessment(assessmentId, token);
        verifyPrivate(assessmentService, times(1)).invoke("convertGooruAssessmentToQuizzesFormat", assessmentContentDto);

        assertEquals("Wrong assessment ID", assessmentId, result.getId());
        assertEquals("Wrong assessment title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", questionTitle, metadataResource.getTitle());
        assertEquals("Wrong question type", trueFalseQuestion, metadataResource.getType());
        assertEquals("Wrong body text", questionTitle, metadataResource.getBody());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResource.getUrl());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void getCollection() throws Exception {
        CollectionDto collectionDto = createCollectionDtoForCollection();
        CollectionContentDto collectionContentDto = new CollectionContentDto();

        when(authenticationRestClient.generateAnonymousToken()).thenReturn(token);
        when(collectionRestClient.getCollection(collectionId, token)).thenReturn(collectionContentDto);
        doReturn(collectionDto).when(assessmentService, "convertGooruCollectionToQuizzesFormat", collectionContentDto);

        CollectionDto result = assessmentService.getCollection(collectionId);

        verify(authenticationRestClient, times(1)).generateAnonymousToken();
        verify(collectionRestClient, times(1)).getCollection(collectionId, token);
        verifyPrivate(assessmentService, times(1)).invoke("convertGooruCollectionToQuizzesFormat", collectionContentDto);

        assertEquals("Wrong assessment ID", collectionId, result.getId());
        assertEquals("Wrong assessment title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertTrue("IsResource is false ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong resource type", imageResource, metadataResource.getType());
        assertEquals("Wrong url", url, metadataResource.getUrl());
        assertNull("Body is not null", metadataResource.getBody());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
    }

    @Test
    public void convertGooruAssessmentToQuizzesFormat() throws Exception {
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();
        assessmentContentDto.setId(assessmentId);
        assessmentContentDto.setTitle(collectionTitle);
        assessmentContentDto.setQuestions(Arrays.asList(createQuestionContentDto()));

        doReturn(Arrays.asList(createResourceDto(resourceId, false, 1, createResourceMetadataDtoForQuestion())))
                .when(assessmentService, "getResources", assessmentContentDto.getQuestions());
        doReturn(createCollectionDtoForAssessment())
                .when(assessmentService, "createCollectionDto", assessmentId, collectionTitle);

        CollectionDto result = WhiteboxImpl.invokeMethod(assessmentService, "convertGooruAssessmentToQuizzesFormat",
                assessmentContentDto);

        verifyPrivate(assessmentService, times(1)).invoke("getResources", assessmentContentDto.getQuestions());
        verifyPrivate(assessmentService, times(1)).invoke("createCollectionDto", assessmentId, collectionTitle);

        assertEquals("Wrong assessment ID", assessmentId, result.getId());
        assertEquals("Wrong assessment title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", questionTitle, metadataResource.getTitle());
        assertEquals("Wrong question type", trueFalseQuestion, metadataResource.getType());
        assertEquals("Wrong body text", questionTitle, metadataResource.getBody());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResource.getUrl());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void convertGooruCollectionToQuizzesFormat() throws Exception {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId);
        collectionContentDto.setTitle(collectionTitle);
        collectionContentDto.setContent(Arrays.asList(createResourceContentDto()));

        doReturn(Arrays.asList(createResourceDto(resourceId, false, 1, createResourceMetadataDtoForResource())))
                .when(assessmentService, "getResources", collectionContentDto.getContent());
        doReturn(createCollectionDtoForCollection())
                .when(assessmentService, "createCollectionDto", collectionId, collectionTitle);

        CollectionDto result = WhiteboxImpl.invokeMethod(assessmentService, "convertGooruCollectionToQuizzesFormat",
                collectionContentDto);

        verifyPrivate(assessmentService, times(1)).invoke("getResources", collectionContentDto.getContent());
        verifyPrivate(assessmentService, times(1)).invoke("createCollectionDto", collectionId, collectionTitle);

        assertEquals("Wrong assessment ID", collectionId, result.getId());
        assertEquals("Wrong assessment title", collectionTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong resource type", imageResource, metadataResource.getType());
        assertEquals("Wrong url", url, metadataResource.getUrl());
        assertNull("Body is not null", metadataResource.getBody());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
    }

    @Test
    public void getResourcesTypeResource() throws Exception {
        List<ResourceContentDto> questions = Arrays.asList(createResourceContentDto());

        doReturn(createResourceMetadataDtoForResource()).when(assessmentService, "mapResource",
                any(ResourceContentDto.class));
        doReturn(null).when(assessmentService, "mapQuestionResource",
                any(ResourceContentDto.class));

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(assessmentService, "getResources", questions);

        verifyPrivate(assessmentService, times(1)).invoke("mapResource", any(ResourceContentDto.class));
        verifyPrivate(assessmentService, times(0)).invoke("mapQuestionResource", any(ResourceContentDto.class));

        assertEquals("Wrong number of resources", 1, result.size());

        ResourceDto resourceResult = result.get(0);
        assertEquals("Wrong resource ID", questionId, resourceResult.getId().toString());
        assertTrue("IsResource is false ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getMetadata();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong resource type", imageResource, metadataResource.getType());
        assertEquals("Wrong url", url, metadataResource.getUrl());
        assertNull("Body is not null", metadataResource.getBody());
        assertNull("Answer is not null", metadataResource.getCorrectAnswer());
        assertNull("Interaction is not null", metadataResource.getInteraction());
    }

    @Test
    public void getResourcesTypeQuestion() throws Exception {
        List<ResourceContentDto> questions = Arrays.asList(createQuestionContentDto());

        doReturn(null).when(assessmentService, "mapResource",
                any(ResourceContentDto.class));
        doReturn(createResourceMetadataDtoForQuestion()).when(assessmentService, "mapQuestionResource",
                any(ResourceContentDto.class));

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(assessmentService, "getResources", questions);

        verifyPrivate(assessmentService, times(0)).invoke("mapResource", any(ResourceContentDto.class));
        verifyPrivate(assessmentService, times(1)).invoke("mapQuestionResource", any(ResourceContentDto.class));

        assertEquals("Wrong number of resources", 1, result.size());

        ResourceDto resourceResult = result.get(0);
        assertEquals("Wrong resource ID", questionId, resourceResult.getId().toString());
        assertFalse("Wrong isResource is true", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResult = resourceResult.getMetadata();
        assertEquals("Wrong title", questionTitle, metadataResult.getTitle());
        assertEquals("Wrong body", questionTitle, metadataResult.getBody());
        assertEquals("Wrong type", trueFalseQuestion, metadataResult.getType());
        assertEquals("Wrong answer size", 1, metadataResult.getCorrectAnswer().size());
        assertNull("Url is not null", metadataResult.getUrl());

        InteractionDto interactionResult = metadataResult.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void mapResource() throws Exception {
        ResourceContentDto resourceContentDto = createResourceContentDto();

        ResourceMetadataDto result =
                WhiteboxImpl.invokeMethod(assessmentService, "mapResource", resourceContentDto);

        assertEquals("Wrong title", resourceTitle, result.getTitle());
        assertEquals("Wrong resource type", imageResource, result.getType());
        assertEquals("Wrong url", url, result.getUrl());
        assertNull("Body is not null", result.getBody());
        assertNull("Answer is not null", result.getCorrectAnswer());
        assertNull("Interaction is not null", result.getInteraction());
    }

    @Test
    public void mapQuestionResource() throws Exception {
        ResourceContentDto resourceContentDto = createQuestionContentDto();

        doReturn(trueFalseQuestion).when(assessmentService, "mapQuestionType", trueFalseQuestion);
        doReturn(Arrays.asList(new AnswerDto("A"))).when(assessmentService, "getCorrectAnswers",
                resourceContentDto.getAnswers());
        doReturn(createInteractionDto()).when(assessmentService, "createInteraction",
                resourceContentDto.getAnswers());

        ResourceMetadataDto result =
                WhiteboxImpl.invokeMethod(assessmentService, "mapQuestionResource", resourceContentDto);

        verifyPrivate(assessmentService, times(1)).invoke("mapQuestionType", trueFalseQuestion);
        verifyPrivate(assessmentService, times(1)).invoke("getCorrectAnswers", resourceContentDto.getAnswers());
        verifyPrivate(assessmentService, times(1)).invoke("createInteraction", resourceContentDto.getAnswers());

        assertEquals("Wrong title", questionTitle, result.getTitle());
        assertEquals("Wrong body", questionTitle, result.getBody());
        assertEquals("Wrong type", trueFalseQuestion, result.getType());
        assertEquals("Wrong type", 1, result.getCorrectAnswer().size());
        assertNull("Url is not null", result.getUrl());

        InteractionDto interactionResult = result.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void createInteraction() throws Exception {
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "text");
        AnswerContentDto answer2 = createAnswerContentDto("2", "1", 2, "text");
        List<AnswerContentDto> answers = Arrays.asList(answer1, answer2);

        InteractionDto result = WhiteboxImpl.invokeMethod(assessmentService, "createInteraction", answers);

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
    public void createCollectionDto() throws Exception {
        CollectionDto result =
                WhiteboxImpl.invokeMethod(assessmentService, "createCollectionDto", collectionId, collectionTitle);

        assertEquals("Wrong id", collectionId, result.getId());
        assertEquals("Wrong title", collectionTitle, result.getMetadata().getTitle());
    }

    @Test
    public void getCorrectAnswers() throws Exception {
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "text");
        AnswerContentDto answer2 = createAnswerContentDto("2", "false", 2, "text");
        List<AnswerContentDto> answers = Arrays.asList(answer1, answer2);

        List<AnswerDto> result = WhiteboxImpl.invokeMethod(assessmentService, "getCorrectAnswers", answers);

        assertEquals("Wrong number of answers", 1, result.size());

        AnswerDto answerResult = result.get(0);
        assertNotNull("Wrong choice Id", answerResult.getValue());
    }

    @Test
    public void mapQuestionType() throws Exception {
        String trueFalseQuestionType = WhiteboxImpl.invokeMethod(assessmentService, "mapQuestionType",
                GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        assertEquals("True/False question type wrongly mapped",
                QuestionTypeEnum.TrueFalse.getLiteral(), trueFalseQuestionType);
        String singleChoiceQuestionType = WhiteboxImpl.invokeMethod(assessmentService, "mapQuestionType",
                GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        assertEquals("MultipleChoice question type wrongly mapped",
                QuestionTypeEnum.SingleChoice.getLiteral(), singleChoiceQuestionType);
        String dragAndDropQuestionType = WhiteboxImpl.invokeMethod(assessmentService, "mapQuestionType",
                GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral());
        assertEquals("DragAndDrop question type wrongly mapped",
                QuestionTypeEnum.DragAndDrop.getLiteral(), dragAndDropQuestionType);
        String noneQuestionType = WhiteboxImpl.invokeMethod(assessmentService, "mapQuestionType",
                "unknown");
        assertEquals("None question type wrongly mapped",
                QuestionTypeEnum.None.getLiteral(), noneQuestionType);
    }

    private ResourceContentDto createQuestionContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setId(questionId);
        resourceContentDto.setTitle(questionTitle);
        resourceContentDto.setSequence(1);
        resourceContentDto.setContentSubformat(trueFalseQuestion);

        AnswerContentDto answer = createAnswerContentDto("1", "1", 1, "text");
        resourceContentDto.setAnswers(Arrays.asList(answer));
        return resourceContentDto;
    }

    private ResourceContentDto createResourceContentDto() {
        ResourceContentDto resourceContentDto = new ResourceContentDto();
        resourceContentDto.setId(questionId);
        resourceContentDto.setTitle(resourceTitle);
        resourceContentDto.setSequence(1);
        resourceContentDto.setContentSubformat(imageResource);
        resourceContentDto.setContentFormat("resource");
        resourceContentDto.setUrl(url);

        return resourceContentDto;
    }

    private AnswerContentDto createAnswerContentDto(String id, String isCorrect, int sequence, String text) {
        AnswerContentDto answer = new AnswerContentDto();
        answer.setIsCorrect(isCorrect);
        answer.setSequence(sequence);
        answer.setAnswerText(text);
        answer.setId(id);
        return answer;
    }

    private CollectionDto createCollectionDtoForAssessment() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId);
        collectionDto.setMetadata(new CollectionMetadataDto(collectionTitle));

        List<ResourceDto> resources = new ArrayList<>();
        resources.add(createResourceDto(resourceId, false, 1, createResourceMetadataDtoForQuestion()));

        collectionDto.setResources(resources);

        return collectionDto;
    }

    private CollectionDto createCollectionDtoForCollection() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId);
        collectionDto.setMetadata(new CollectionMetadataDto(collectionTitle));

        List<ResourceDto> resources = new ArrayList<>();
        resources.add(createResourceDto(resourceId, true, 1, createResourceMetadataDtoForResource()));

        collectionDto.setResources(resources);

        return collectionDto;
    }

    private ResourceMetadataDto createResourceMetadataDtoForQuestion() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(questionTitle);
        metadata.setType(trueFalseQuestion);
        metadata.setCorrectAnswer(Arrays.asList(new AnswerDto("A")));
        metadata.setInteraction(createInteractionDto());
        metadata.setBody(questionTitle);
        return metadata;
    }

    private ResourceMetadataDto createResourceMetadataDtoForResource() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceTitle);
        metadata.setType(imageResource);
        metadata.setUrl(url);
        return metadata;
    }

    private InteractionDto createInteractionDto() {
        InteractionDto interactionDto = new InteractionDto();
        interactionDto.setPrompt("");
        interactionDto.setShuffle(false);
        interactionDto.setMaxChoices(0);
        interactionDto.setChoices(Arrays.asList(
                createChoiceDto(true, "text", "A", 1), createChoiceDto(true, "text2", "B", 2)));
        return interactionDto;
    }

    private ChoiceDto createChoiceDto(boolean fixed, String text, String value, int sequence) {
        ChoiceDto choiceDto = new ChoiceDto();
        choiceDto.setFixed(fixed);
        choiceDto.setText(text);
        choiceDto.setValue(value);
        choiceDto.setSequence(sequence);
        return choiceDto;
    }

    private ResourceDto createResourceDto(UUID id, boolean isResource, int sequence,
                                          ResourceMetadataDto metadata) {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(id);
        resourceDto.setIsResource(isResource);
        resourceDto.setSequence(sequence);
        resourceDto.setMetadata(metadata);
        return resourceDto;
    }

}