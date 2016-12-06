package com.quizzes.api.content.gooru.service;

import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ResourceService;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AnswerDto;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.dto.QuestionDto;
import com.quizzes.api.content.gooru.enums.GooruQuestionTypeEnum;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CollectionContentService.class)
public class CollectionContentServiceImplTest {

    @InjectMocks
    private CollectionContentService collectionContentService = Mockito.spy(CollectionContentServiceImpl.class);

    @Mock
    CollectionRestClient collectionRestClient;

    @Mock
    CollectionService collectionService;

    @Mock
    ResourceService resourceService;

    @Test
    public void createCollectionCopy() throws Exception {
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
        when(collectionRestClient.getAssessment(any(String.class))).thenReturn(assessmentDto);

        Collection collection = new Collection();
        collection.setId(UUID.randomUUID());
        when(collectionService.save(any(Collection.class))).thenReturn(collection);

        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        when(resourceService.save(any(Resource.class))).thenReturn(resource);

        Profile owner = new Profile();
        owner.setId(UUID.randomUUID());

        String externalCollectionId = UUID.randomUUID().toString();
        Collection copiedCollection = collectionContentService.createCollectionCopy(externalCollectionId, owner);

        verify(collectionRestClient, times(1)).getAssessment(externalCollectionId);
        verify(collectionService, times(1)).save(any(Collection.class));
        verify(resourceService, atLeast(1)).save(any(Resource.class));

        assertNotNull("Copied Collection is null", copiedCollection);
        assertEquals("Copied Collection ID is different", collection.getId(), copiedCollection.getId());
    }

    @Test
    public void createInteraction() throws Exception {
        AnswerDto answerTrueFalse1 = new AnswerDto();
        answerTrueFalse1.setAnswerText("Answer True False 1 text");
        answerTrueFalse1.setIsCorrect("true");
        answerTrueFalse1.setSequence(1);
        AnswerDto answerTrueFalse2 = new AnswerDto();
        answerTrueFalse2.setAnswerText("Answer True False 1 text");
        answerTrueFalse2.setIsCorrect("false");
        answerTrueFalse2.setSequence(2);
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerTrueFalse1);
        answers.add(answerTrueFalse2);

        Map<String, Object> interaction =
                WhiteboxImpl.invokeMethod(collectionContentService, "createInteraction", answers);

        assertEquals("Wrong value for interaction shuffle", false, interaction.get("shuffle"));
        assertEquals("Wrong value for interaction maxChoices", 0, interaction.get("maxChoices"));
        assertEquals("Wrong value for interaction prompt", "", interaction.get("prompt"));
        List<Map<String, Object>> choices =  (List<Map<String, Object>>) interaction.get("choices");
        assertEquals("Wrong number of interaction choices", 2, choices.size());
        assertEquals("Wrong value for choice text", answerTrueFalse1.getAnswerText(), choices.get(0).get("text"));
        assertEquals("Wrong value for choice value", answerTrueFalse1.getAnswerText(), choices.get(0).get("value"));
        assertEquals("Wrong value for choice sequence", answerTrueFalse1.getSequence(), choices.get(0).get("sequence"));
        assertEquals("Wrong value for choice isFixed", true, choices.get(0).get("isFixed"));
    }

    @Test
    public void mapQuestionType() throws Exception {
        String trueFalseQuestionType = WhiteboxImpl.invokeMethod(collectionContentService, "mapQuestionType",
                GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        assertEquals("True/False question type wrongly mapped",
                QuestionTypeEnum.TrueFalse.getLiteral(), trueFalseQuestionType);
        String singleChoiceQuestionType = WhiteboxImpl.invokeMethod(collectionContentService, "mapQuestionType",
                GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        assertEquals("MultipleChoice question type wrongly mapped",
                QuestionTypeEnum.SingleChoice.getLiteral(), singleChoiceQuestionType);
        String noneQuestionType = WhiteboxImpl.invokeMethod(collectionContentService, "mapQuestionType",
                "unknown");
        assertEquals("None question type wrongly mapped",
                QuestionTypeEnum.None.getLiteral(), noneQuestionType);
    }

    @Test
    public void getCorrectAnswers() throws Exception {
        AnswerDto answerTrueFalse1 = new AnswerDto();
        answerTrueFalse1.setAnswerText("Answer True False 1 text");
        answerTrueFalse1.setIsCorrect("true");
        answerTrueFalse1.setSequence(1);
        AnswerDto answerTrueFalse2 = new AnswerDto();
        answerTrueFalse2.setAnswerText("Answer True False 1 text");
        answerTrueFalse2.setIsCorrect("false");
        answerTrueFalse2.setSequence(2);
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerTrueFalse1);
        answers.add(answerTrueFalse2);

        List<Map<String, String>> correctAnswers =
                WhiteboxImpl.invokeMethod(collectionContentService, "getCorrectAnswers", answers);

        assertEquals("Wrong number of correct answers", 1, correctAnswers.size());
        assertEquals("Wrong answer value",
                answerTrueFalse1.getAnswerText(), correctAnswers.get(0).get("value"));
    }

}
