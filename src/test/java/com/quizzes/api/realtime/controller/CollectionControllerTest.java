package com.quizzes.api.realtime.controller;

import com.google.common.collect.Lists;
import com.quizzes.api.common.dto.controller.response.AnswerDTO;
import com.quizzes.api.common.dto.controller.response.ChoiceDTO;
import com.quizzes.api.common.dto.controller.response.CollectionDataDTO;
import com.quizzes.api.common.dto.controller.response.CollectionDataResourceDTO;
import com.quizzes.api.common.dto.controller.response.InteractionDTO;
import com.quizzes.api.common.dto.controller.response.QuestionDataDTO;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ResourceService;
import com.quizzes.api.realtime.model.CollectionOnAir;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionControllerTest {
    @InjectMocks
    private CollectionController collectionController = new CollectionController();

    @Mock
    private CollectionService collectionService;

    @Mock
    private ResourceService resourceService;

    HttpServletResponse response = mock(HttpServletResponse.class);

    @Test
    public void getCollection() throws Exception {

        List<AnswerDTO> answers = new ArrayList<>();
        AnswerDTO answerDto = new AnswerDTO("A");
        answers.add(answerDto);
        List<ChoiceDTO> choices = new ArrayList<>();
        ChoiceDTO choiceDto1 = new ChoiceDTO("option 1", false, "A");
        choices.add(choiceDto1);
        ChoiceDTO choiceDto2 = new ChoiceDTO("option 2", false, "B");
        choices.add(choiceDto2);
        ChoiceDTO choiceDto3 = new ChoiceDTO("option 3", false, "C");
        choices.add(choiceDto3);
        InteractionDTO interactionDto = new InteractionDTO(true, 10, "mocked Interaction", choices);
        QuestionDataDTO questionDataDto = new QuestionDataDTO("mocked Question Data", QuestionTypeEnum.SingleChoice, answers, "mocked body", interactionDto);
        CollectionDataResourceDTO collectionDataResourceDto1 = new CollectionDataResourceDTO(UUID.randomUUID(),
                true, questionDataDto);
        List<CollectionDataResourceDTO> collectionDataResourceDtos = new ArrayList<>();
        collectionDataResourceDtos.add(collectionDataResourceDto1);

        List<AnswerDTO> answers2 = new ArrayList<>();
        AnswerDTO answerDto2 = new AnswerDTO("T");
        answers2.add(answerDto2);
        List<ChoiceDTO> choices2 = new ArrayList<>();
        ChoiceDTO choiceDto4 = new ChoiceDTO("True", false, "T");
        choices2.add(choiceDto4);
        ChoiceDTO choiceDto5 = new ChoiceDTO("False", false, "F");
        choices2.add(choiceDto5);
        InteractionDTO interactionDto2 = new InteractionDTO(true, 10, "mocked Interaction", choices2);
        QuestionDataDTO questionDataDto2 = new QuestionDataDTO("mocked Question Data", QuestionTypeEnum.TrueFalse, answers2, "mocked body", interactionDto2);
        CollectionDataResourceDTO collectionDataResourceDto2 = new CollectionDataResourceDTO(UUID.randomUUID(),
                true, questionDataDto2);
        collectionDataResourceDtos.add(collectionDataResourceDto2);

        CollectionDataDTO collectionDto = new CollectionDataDTO(UUID.randomUUID(), false, collectionDataResourceDtos);

        when(collectionService.getCollection(any(UUID.class))).thenReturn(collectionDto);

        ResponseEntity<CollectionDataDTO> result = collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());

        verify(collectionService, times(1)).getCollection(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNotNull("Response Body is Null", result.getBody());
        assertSame(result.getBody().getClass(), CollectionDataDTO.class);
    }

    @Test
    public void findCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        when(collectionService.findCollectionOnAir("classId", "collectionId")).thenReturn(mockCollection);

        CollectionOnAir result = collectionController.findCollectionOnAir("classId", "collectionId", response);
        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        assertNotNull(result);
        assertEquals(mockCollection.getCollectionId(), "collectionId");
        assertEquals(mockCollection.getClassId(), "classId");
    }

    @Test
    public void findCollectionOnAirNotFound() throws Exception {
        when(collectionService.findCollectionOnAir("classId", "collectionId")).thenReturn(null);

        CollectionOnAir result = collectionController.findCollectionOnAir("classId", "collectionId", response);
        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(response, times(1)).setStatus(HttpStatus.NOT_FOUND.value());
        assertNull(result);
    }

    @Test
    public void findCollectionsOnAir() throws Exception {
        List<CollectionOnAir> collections = new ArrayList<>();
        collections.add(new CollectionOnAir("classId", "firstCollection"));
        collections.add(new CollectionOnAir("classId", "secondCollection"));
        when(collectionService.findCollectionsOnAirByClass("classId")).thenReturn(collections);

        Iterable<CollectionOnAir> result = collectionController.findCollectionsOnAir("classId");
        verify(collectionService, times(1)).findCollectionsOnAirByClass(Mockito.eq("classId"));

        // Creating the list to verify the size
        List<CollectionOnAir> resultList = Lists.newArrayList(result);
        assertEquals(resultList.size(), 2);

        assertEquals(resultList.get(0).getCollectionId(), "firstCollection");
        assertEquals(resultList.get(1).getCollectionId(), "secondCollection");

        assertNotEquals(resultList.get(0).getCollectionId(), "secondCollection");
        assertNotEquals(resultList.get(1).getCollectionId(), "firstCollection");

        assertNotNull(result);
    }

    @Test
    public void addCollectionOnAir() throws Exception {
        collectionController.addCollectionOnAir("classId", "collectionId");
        verify(collectionService, times(1)).addCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
    }

    @Test
    public void removeCollectionOnAir() throws Exception {
        collectionController.removeCollectionOnAir("classId", "collectionId");
        verify(collectionService, times(1)).removeCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
    }

    @Test
    public void completeCollection() throws Exception {
        collectionController.completeCollection("classId", "collectionId", "userId");
        verify(collectionService, times(1)).completeCollectionForUser(Mockito.eq("classId_collectionId"), Mockito.eq("userId"));
    }

    @Test
    public void resetCollection() throws Exception {
        collectionController.resetCollection("classId", "collectionId", "userId");
        verify(collectionService, times(1)).resetCollectionForUser(Mockito.eq("classId_collectionId"), Mockito.eq("userId"));
    }

}