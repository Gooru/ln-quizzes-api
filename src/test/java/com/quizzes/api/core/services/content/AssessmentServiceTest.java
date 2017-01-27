package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AssessmentMetadataDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.InteractionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.QuestionContentDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
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
import static org.junit.Assert.assertTrue;
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
    private AuthenticationRestClient authenticationRestClient;

    private String assessmentId;
    private UUID resourceId;
    private String questionId;
    private String assessmentTitle;
    private String resourceTitle;
    private String questionTitle;
    private String trueFalseQuestion;
    private String token;

    @Before
    public void before() throws Exception {
        assessmentId = UUID.randomUUID().toString();
        resourceId = UUID.randomUUID();
        questionId = UUID.randomUUID().toString();
        assessmentTitle = "Assessment title";
        resourceTitle = "Resource title";
        questionTitle = "Question title";
        trueFalseQuestion = "true_false";
        token = "token-id";
    }

    @Test
    public void getAssessment() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();

        when(authenticationRestClient.generateAnonymousToken()).thenReturn(token);
        when(assessmentRestClient.getAssessment(assessmentId, token)).thenReturn(assessmentContentDto);
        doReturn(collectionDto).when(assessmentService, "convertGooruAssessmentToQuizzesFormat", assessmentContentDto);

        CollectionDto result = assessmentService.getAssessment(assessmentId);

        verify(authenticationRestClient, times(1)).generateAnonymousToken();
        verify(assessmentRestClient, times(1)).getAssessment(assessmentId, token);
        verifyPrivate(assessmentService, times(1)).invoke("convertGooruAssessmentToQuizzesFormat", assessmentContentDto);

        assertEquals("Wrong assessment ID", assessmentId, result.getId());
        assertEquals("Wrong assessment title", assessmentTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getQuestionData();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong body text", resourceTitle, metadataResource.getBody());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    @Test
    public void convertGooruAssessmentToQuizzesFormat() throws Exception {
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();
        assessmentContentDto.setId(assessmentId);
        assessmentContentDto.setTitle(assessmentTitle);
        assessmentContentDto.setQuestions(Arrays.asList(createQuestionContentDto()));

        when(authenticationRestClient.generateAnonymousToken()).thenReturn(token);
        when(assessmentRestClient.getAssessment(assessmentId, token)).thenReturn(assessmentContentDto);
        doReturn(Arrays.asList(createResourceDto(resourceId, false, 1, createResourceMetadataDto())))
                .when(assessmentService, "getResources", assessmentContentDto.getQuestions());

        CollectionDto result = WhiteboxImpl.invokeMethod(assessmentService, "convertGooruAssessmentToQuizzesFormat",
                assessmentContentDto);

        verifyPrivate(assessmentService, times(1)).invoke("getResources", assessmentContentDto.getQuestions());

        assertEquals("Wrong assessment ID", assessmentId, result.getId());
        assertEquals("Wrong assessment title", assessmentTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());
    }

    @Test
    public void getResources() throws Exception {
        List<QuestionContentDto> questions = Arrays.asList(createQuestionContentDto());

        doReturn(trueFalseQuestion).when(assessmentService, "mapQuestionType", trueFalseQuestion);
        doReturn(Arrays.asList(new AnswerDto("A"))).when(assessmentService, "getCorrectAnswers",
                questions.get(0).getAnswers());
        doReturn(createInteractionDto()).when(assessmentService, "createInteraction",
                questions.get(0).getAnswers());

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(assessmentService, "getResources", questions);

        verifyPrivate(assessmentService, times(1)).invoke("mapQuestionType", trueFalseQuestion);
        verifyPrivate(assessmentService, times(1)).invoke("getCorrectAnswers", questions.get(0).getAnswers());
        verifyPrivate(assessmentService, times(1)).invoke("createInteraction", questions.get(0).getAnswers());

        assertEquals("Wrong number of resources", 1, result.size());

        ResourceDto resourceResult = result.get(0);
        assertEquals("Wrong resource ID", questionId, resourceResult.getId().toString());
        assertFalse("Wrong isResource is true", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResult = resourceResult.getQuestionData();
        assertEquals("Wrong title", questionTitle, metadataResult.getTitle());
        assertEquals("Wrong body", questionTitle, metadataResult.getBody());
        assertEquals("Wrong type", trueFalseQuestion, metadataResult.getType());
        assertEquals("Wrong type", 1, metadataResult.getCorrectAnswer().size());

        InteractionDto interactionResult = metadataResult.getInteraction();
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
        assertEquals("Wrong choice Id", "1", choiceResult.getValue());
        assertEquals("Wrong sequence", 1, choiceResult.getSequence());
        assertEquals("Wrong text", "text", choiceResult.getText());
        assertTrue("Shuffle is false", choiceResult.getIsFixed());
    }

    @Test
    public void getCorrectAnswers() throws Exception {
        AnswerContentDto answer1 = createAnswerContentDto("1", "1", 1, "text");
        AnswerContentDto answer2 = createAnswerContentDto("2", "false", 2, "text");
        List<AnswerContentDto> answers = Arrays.asList(answer1, answer2);

        List<AnswerDto> result = WhiteboxImpl.invokeMethod(assessmentService, "getCorrectAnswers", answers);

        assertEquals("Wrong number of answers", 1, result.size());

        AnswerDto answerResult = result.get(0);
        assertEquals("Wrong choice Id", "1", answerResult.getValue());
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

    private QuestionContentDto createQuestionContentDto() {
        QuestionContentDto questionContentDto = new QuestionContentDto();
        questionContentDto.setId(questionId);
        questionContentDto.setTitle(questionTitle);
        questionContentDto.setSequence(1);
        questionContentDto.setContentSubformat(trueFalseQuestion);

        AnswerContentDto answer = createAnswerContentDto("1", "1", 1, "text");
        questionContentDto.setAnswers(Arrays.asList(answer));
        return questionContentDto;
    }

    private AnswerContentDto createAnswerContentDto(String id, String isCorrect, int sequence, String text) {
        AnswerContentDto answer = new AnswerContentDto();
        answer.setIsCorrect(isCorrect);
        answer.setSequence(sequence);
        answer.setAnswerText(text);
        answer.setId(id);
        return answer;
    }

    private CollectionDto createCollectionDto() {
        AssessmentMetadataDto metadata = new AssessmentMetadataDto();
        metadata.setTitle(assessmentTitle);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId);
        collectionDto.setMetadata(metadata);

        List<ResourceDto> resources = new ArrayList<>();
        resources.add(createResourceDto(resourceId, false, 1, createResourceMetadataDto()));

        collectionDto.setResources(resources);

        return collectionDto;
    }

    private ResourceMetadataDto createResourceMetadataDto() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceTitle);
        metadata.setType(trueFalseQuestion);
        metadata.setCorrectAnswer(Arrays.asList(new AnswerDto("A")));
        metadata.setInteraction(createInteractionDto());
        metadata.setBody(resourceTitle);
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

    private ResourceDto createResourceDto(UUID id, boolean isResource, int sequence, ResourceMetadataDto metadata) {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(id);
        resourceDto.setIsResource(isResource);
        resourceDto.setSequence(sequence);
        resourceDto.setQuestionData(metadata);
        return resourceDto;
    }

}