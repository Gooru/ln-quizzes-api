package com.quizzes.api.core.controllers;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.dtos.QuestionDataDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.jooq.enums.Lms;
import com.quizzes.api.core.services.CollectionService;
import com.quizzes.api.core.services.ResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CollectionController.class)
public class CollectionControllerTest {
    @InjectMocks
    private CollectionController collectionController = Mockito.spy(CollectionController.class);

    @Mock
    private CollectionService collectionService;

    @Mock
    private ResourceService resourceService;

    @Mock
    Gson gson = new Gson();

    @Test
    public void getCollectionWhenIdExists() throws Exception {
        AnswerDto answer = new AnswerDto();
        answer.setValue("A");
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answer);

        QuestionDataDto questionDataDto = new QuestionDataDto();
        questionDataDto.setTitle("question 1");
        questionDataDto.setType("true_false");
        questionDataDto.setBody("question 1");
        questionDataDto.setCorrectAnswer(answers);

        ResourceDto resource1 = new ResourceDto();
        UUID resourceId1 = UUID.randomUUID();
        resource1.setId(resourceId1);
        resource1.setSequence(1);
        resource1.setIsResource(false);
        resource1.setQuestionData(questionDataDto);

        ResourceDto resource2 = new ResourceDto();
        UUID resourceId2 = UUID.randomUUID();
        resource2.setId(resourceId2);
        resource2.setIsResource(false);
        resource2.setSequence(2);
        resource1.setQuestionData(questionDataDto);

        List<ResourceDto> resources = new ArrayList<>();
        resources.add(resource1);
        resources.add(resource2);

        CollectionGetResponseDto collectionDto = new CollectionGetResponseDto();
        UUID collectionId = UUID.randomUUID();
        collectionDto.setId(collectionId);
        collectionDto.setIsCollection(false);
        collectionDto.setResources(resources);

        when(collectionService.findCollectionById(any(UUID.class))).thenReturn(collectionDto);

        ResponseEntity<CollectionGetResponseDto> result =
                collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());

        verify(collectionService, times(1)).findCollectionById(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        CollectionGetResponseDto response = result.getBody();
        assertNotNull("Response Body is Null", response);
        assertFalse("IsCollection is true", response.getIsCollection());
        assertEquals("Wrong size in resources", 2, response.getResources().size());
        assertEquals("Wrong type in question", "true_false", response.getResources().get(0).getQuestionData().getType());
        assertEquals("Wrong title in question", "question 1", response.getResources().get(0).getQuestionData().getTitle());

        ResourceDto responseResource = response.getResources().get(1);
        assertEquals("Wrong size in resources", 2, responseResource.getSequence());
        assertEquals("Wrong id for resource 2", resourceId2, responseResource.getId());
        assertFalse("Wrong id for resource 2", responseResource.getIsResource());
        assertEquals("Wrong type in question", "true_false", response.getResources().get(0).getQuestionData().getType());
        assertEquals("Wrong title in question", "question 1", response.getResources().get(0).getQuestionData().getTitle());
        assertSame(result.getBody().getClass(), CollectionGetResponseDto.class);
    }

    @Test(expected = ContentNotFoundException.class)
    public void getCollectionWhenThrowsContentNotFoundException() throws Exception {
        when(collectionService.findCollectionById(any(UUID.class))).thenThrow(ContentNotFoundException.class);
        collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());
    }

}