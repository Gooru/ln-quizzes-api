package com.quizzes.api.content.gooru.service;

import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        answerMultipleChoice1.setIsCorrect(false);
        answerMultipleChoice1.setSequence(1);

        AnswerDto answerMultipleChoice2 = new AnswerDto();
        answerMultipleChoice2.setAnswerText("Answer Multiple Choice 2 text");
        answerMultipleChoice2.setIsCorrect(true);
        answerMultipleChoice2.setSequence(2);

        AnswerDto answerMultipleChoice3 = new AnswerDto();
        answerMultipleChoice3.setAnswerText("Answer Multiple Choice 3 text");
        answerMultipleChoice3.setIsCorrect(true);
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
        answerTrueFalse1.setIsCorrect(true);
        answerTrueFalse1.setSequence(1);

        AnswerDto answerTrueFalse2 = new AnswerDto();
        answerTrueFalse2.setAnswerText("Answer True False 1 text");
        answerTrueFalse2.setIsCorrect(true);
        answerTrueFalse2.setSequence(1);

        List<AnswerDto> answerTrueFalseList = new ArrayList<>();
        answerTrueFalseList.add(answerTrueFalse1);
        answerTrueFalseList.add(answerTrueFalse1);

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

}
