package com.quizzes.api.content.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.AnswerDto;
import com.quizzes.api.core.dtos.content.AssessmentDto;
import com.quizzes.api.core.dtos.content.QuestionDto;
import com.quizzes.api.core.dtos.content.UserDataTokenDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import com.quizzes.api.core.services.content.CollectionService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CollectionService.class, Gson.class})
public class CollectionServiceTest {

    @InjectMocks
    private CollectionService collectionService = spy(new CollectionService());

    @Mock
    private CollectionRestClient collectionRestClient;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    @Mock
    private Gson gson;

    /**
     * Tests {@link CollectionService#createCollection(String, Profile)} using an original Assessment
     * @throws Exception
     */
    @Ignore
    @Test
    public void createCollectionFromOriginalAssessment() throws Exception {
        AssessmentDto assessmentDto = createTestAssessmentDto();

        UserDataTokenDto userDataTokenDto = new UserDataTokenDto();
        userDataTokenDto.setFirstName("Bryan");
        userDataTokenDto.setLastName("Oviedo");
        userDataTokenDto.setEmail("boviedo@gooru.org");

        when(gson.fromJson(any(String.class), anyObject())).thenReturn(userDataTokenDto);

        when(authenticationRestClient.generateUserToken(any(UserDataTokenDto.class))).thenReturn("user-token");

        assessmentDto.setOwnerId(UUID.randomUUID().toString());
        when(collectionRestClient.getAssessment(any(String.class), any(String.class))).thenReturn(assessmentDto);

        verify(gson, times(1)).fromJson(any(String.class), anyObject());
        verify(authenticationRestClient, times(1)).generateUserToken(any(UserDataTokenDto.class));
        // copyAssessment is not called, this means the assessment owner is the same user creating the collection
        verify(collectionRestClient, times(0)).copyAssessment(any(String.class), any(String.class));
        verify(collectionRestClient, times(1)).getAssessment(any(String.class), any(String.class));
        //verify(collectionService, times(1)).save(any(Collection.class));
        //verify(resourceService, times(2)).save(any(Resource.class));

        //assertNotNull("The new collection is null", newCollection);
    }

    /**
     * Tests {@link CollectionService#createCollection(String, Profile)} using an original Assessment
     * @throws Exception
     */
    @Ignore
    @Test
    public void createCollectionFromCopiedAssessment() throws Exception {
        AssessmentDto assessmentDto = createTestAssessmentDto();

        UserDataTokenDto userDataTokenDto = new UserDataTokenDto();
        userDataTokenDto.setFirstName("Bryan");
        userDataTokenDto.setLastName("Oviedo");
        userDataTokenDto.setEmail("boviedo@gooru.org");

        when(gson.fromJson(any(String.class), anyObject())).thenReturn(userDataTokenDto);

        when(authenticationRestClient.generateUserToken(any(UserDataTokenDto.class))).thenReturn("user-token");

        assessmentDto.setOwnerId(UUID.randomUUID().toString());
        when(collectionRestClient.getAssessment(any(String.class), any(String.class))).thenReturn(assessmentDto);


        String externalCollectionId = UUID.randomUUID().toString();

        //Collection newCollection = collectionService.createCollection(externalCollectionId, owner);

        verify(gson, times(1)).fromJson(any(String.class), anyObject());
        verify(authenticationRestClient, times(1)).generateUserToken(any(UserDataTokenDto.class));
        verify(collectionRestClient, times(1)).copyAssessment(any(String.class), any(String.class));
        verify(collectionRestClient, times(2)).getAssessment(any(String.class), any(String.class));
        //verify(collectionService, times(1)).save(any(Collection.class));
        //verify(resourceService, times(2)).save(any(Resource.class));

        //assertNotNull("The new collection is null", newCollection);
    }

    /**
     * Tests private method {@link CollectionService#createCollectionFromAssessment(AssessmentDto, String, UUID)}
     */
    @Ignore
    @Test
    public void createCollectionFromAssessment() throws Exception {
        AssessmentDto assessmentDto = createTestAssessmentDto();

        //Collection collection = createTestCollection(assessmentDto);

        //when(collectionService.save(any(Collection.class))).thenReturn(collection);

        //when(resourceService.save(any(Resource.class))).thenReturn(null);

        WhiteboxImpl.invokeMethod(collectionService, "createCollectionFromAssessment", assessmentDto, UUID.randomUUID().toString(), UUID.randomUUID());

        //saves the new Collection
        //verify(collectionService, times(1)).save(any(Collection.class));

        //saves the two Questions
        //verify(resourceService, times(2)).save(any(Resource.class));
    }

    @Ignore
    @Test
    public void createCollectionCopy() throws Exception {
        AssessmentDto assessmentDto = createTestAssessmentDto();

        //Collection collection = createTestCollection(assessmentDto);

        //when(collectionService.save(any(Collection.class))).thenReturn(collection);

        //doReturn(new Resource()).when(resourceService).save(any(Resource.class));

        doReturn("copiedAssessmentID").when(collectionRestClient).copyAssessment(any(String.class), any(String.class));

        doReturn(assessmentDto).when(collectionRestClient).getAssessment(any(String.class), any(String.class));

        WhiteboxImpl.invokeMethod(collectionService, "createCollectionCopy", "assessmentID", UUID.randomUUID(), "userToken");

        //verify(collectionService, times(1)).save(any(Collection.class));
        //verify(resourceService, times(2)).save(any(Resource.class));
        verify(collectionRestClient, times(1)).copyAssessment(any(String.class), any(String.class));
        //verify(collectionService, times(1)).save(any(Collection.class));
        //verify(resourceService, times(2)).save(any(Resource.class));
        verify(collectionRestClient, times(1)).copyAssessment(any(String.class),any(String.class));
        verify(collectionRestClient, times(1)).getAssessment(any(String.class), any(String.class));

    }

    @Test
    public void createInteraction() throws Exception {
        String answerId1 = UUID.randomUUID().toString();
        AnswerDto answerTrueFalse1 = createAnswerDto(answerId1, "Answer True False 1 text", "true", 1);

        String answerId2 = UUID.randomUUID().toString();
        AnswerDto answerTrueFalse2 = createAnswerDto(answerId2, "Answer True False 1 text", "false", 2);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerTrueFalse1);
        answers.add(answerTrueFalse2);

        Map<String, Object> interaction =
                WhiteboxImpl.invokeMethod(collectionService, "createInteraction", answers);

        assertEquals("Wrong value for interaction shuffle", false, interaction.get("shuffle"));
        assertEquals("Wrong value for interaction maxChoices", 0, interaction.get("maxChoices"));
        assertEquals("Wrong value for interaction prompt", "", interaction.get("prompt"));
        List<Map<String, Object>> choices = (List<Map<String, Object>>) interaction.get("choices");
        assertEquals("Wrong number of interaction choices", 2, choices.size());
        assertEquals("Wrong value for choice text", answerTrueFalse1.getAnswerText(), choices.get(0).get("text"));
        assertEquals("Wrong value for choice value", answerTrueFalse1.getId(), choices.get(0).get("value"));
        assertEquals("Wrong value for choice sequence", answerTrueFalse1.getSequence(), choices.get(0).get("sequence"));
        assertEquals("Wrong value for choice isFixed", true, choices.get(0).get("isFixed"));
    }

    @Test
    public void mapQuestionType() throws Exception {
        String trueFalseQuestionType = WhiteboxImpl.invokeMethod(collectionService, "mapQuestionType",
                GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        assertEquals("True/False question type wrongly mapped",
                QuestionTypeEnum.TrueFalse.getLiteral(), trueFalseQuestionType);
        String singleChoiceQuestionType = WhiteboxImpl.invokeMethod(collectionService, "mapQuestionType",
                GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        assertEquals("MultipleChoice question type wrongly mapped",
                QuestionTypeEnum.SingleChoice.getLiteral(), singleChoiceQuestionType);
        String dragAndDropQuestionType = WhiteboxImpl.invokeMethod(collectionService, "mapQuestionType",
                GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral());
        assertEquals("DragAndDrop question type wrongly mapped",
                QuestionTypeEnum.DragAndDrop.getLiteral(), dragAndDropQuestionType);
        String noneQuestionType = WhiteboxImpl.invokeMethod(collectionService, "mapQuestionType",
                "unknown");
        assertEquals("None question type wrongly mapped",
                QuestionTypeEnum.None.getLiteral(), noneQuestionType);
    }

    @Test
    public void getCorrectAnswers() throws Exception {
        String answerId1 = UUID.randomUUID().toString();
        AnswerDto answerTrueFalse1 = createAnswerDto(answerId1, "Answer True False 1 text", "true", 1);

        String answerId2 = UUID.randomUUID().toString();
        AnswerDto answerTrueFalse2 = createAnswerDto(answerId2, "Answer True False 1 text", "false", 2);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerTrueFalse1);
        answers.add(answerTrueFalse2);

        List<Map<String, String>> correctAnswers =
                WhiteboxImpl.invokeMethod(collectionService, "getCorrectAnswers", answers);

        assertEquals("Wrong number of correct answers", 1, correctAnswers.size());
        assertEquals("Wrong answer value",
                answerTrueFalse1.getId(), correctAnswers.get(0).get("value"));
    }

    @Ignore
    @Test
    public void copyQuestions() throws Exception {
        AssessmentDto assessmentDto = createTestAssessmentDto();

        //Collection collection = createTestCollection(assessmentDto);
        UUID ownerId = UUID.randomUUID();

        doReturn(null).when(collectionService, "mapQuestionType", any(List.class));
        doReturn(null).when(collectionService, "getCorrectAnswers", any(List.class));
        doReturn(null).when(collectionService, "createInteraction", any(List.class));
        //when(resourceService.save(any(Resource.class))).thenReturn(new Resource());

        //WhiteboxImpl.invokeMethod(collectionService, "copyQuestions", collection,
        //        ownerId, assessmentDto.getQuestions());

        verifyPrivate(collectionService, times(2)).invoke("mapQuestionType", any(List.class));
        verifyPrivate(collectionService, times(2)).invoke("getCorrectAnswers", any(List.class));
        verifyPrivate(collectionService, times(2)).invoke("createInteraction", any(List.class));
        //verify(resourceService, times(2)).save(any(Resource.class));
    }

    private AssessmentDto createTestAssessmentDto() {
        AnswerDto answerMultipleChoice1 = new AnswerDto();
        answerMultipleChoice1.setAnswerText("Answer Multiple Choice 1 text");
        answerMultipleChoice1.setIsCorrect("false");
        answerMultipleChoice1.setSequence(1);

        AnswerDto answerMultipleChoice2 = new AnswerDto();
        answerMultipleChoice2.setAnswerText("Answer Multiple Choice 2 text");
        answerMultipleChoice2.setIsCorrect("true");
        answerMultipleChoice2.setSequence(2);

        AnswerDto answerMultipleChoice3 = new AnswerDto();
        answerMultipleChoice3.setAnswerText("Answer Multiple Choice 3 text");
        answerMultipleChoice3.setIsCorrect("true");
        answerMultipleChoice3.setSequence(2);

        List<AnswerDto> answerMultipleChoiceList = new ArrayList<>();
        answerMultipleChoiceList.add(answerMultipleChoice1);
        answerMultipleChoiceList.add(answerMultipleChoice2);
        answerMultipleChoiceList.add(answerMultipleChoice3);

        QuestionDto questionMultipleChoice = new QuestionDto();
        questionMultipleChoice.setId(UUID.randomUUID().toString());
        questionMultipleChoice.setTitle("Question 1 Title");
        questionMultipleChoice.setSequence(1);
        questionMultipleChoice.setContentSubformat(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        questionMultipleChoice.setAnswers(answerMultipleChoiceList);

        AnswerDto answerTrueFalse1 = new AnswerDto();
        answerTrueFalse1.setAnswerText("Answer True False 1 text");
        answerTrueFalse1.setIsCorrect("true");
        answerTrueFalse1.setSequence(1);

        AnswerDto answerTrueFalse2 = new AnswerDto();
        answerTrueFalse2.setAnswerText("Answer True False 2 text");
        answerTrueFalse2.setIsCorrect("false");
        answerTrueFalse2.setSequence(2);

        List<AnswerDto> answerTrueFalseList = new ArrayList<>();
        answerTrueFalseList.add(answerTrueFalse1);
        answerTrueFalseList.add(answerTrueFalse2);

        QuestionDto questionTrueFalse = new QuestionDto();
        questionTrueFalse.setId(UUID.randomUUID().toString());
        questionTrueFalse.setTitle("Question 2 Title");
        questionTrueFalse.setSequence(2);
        questionTrueFalse.setContentSubformat(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        questionTrueFalse.setAnswers(answerTrueFalseList);

        List<QuestionDto> questionList = new ArrayList<>();
        questionList.add(questionMultipleChoice);
        questionList.add(questionTrueFalse);

        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setId(UUID.randomUUID().toString());
        assessmentDto.setTitle("Assessment Title");
        assessmentDto.setQuestions(questionList);

        return assessmentDto;
    }

    private AnswerDto createAnswerDto(String id, String answerText, String isCorrect, int sequence) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(id);
        answerDto.setAnswerText(answerText);
        answerDto.setIsCorrect(isCorrect);
        answerDto.setSequence(sequence);
        return answerDto;
    }
}
