package com.quizzes.api.realtime.controller;

import com.google.common.collect.Lists;
import com.quizzes.api.common.dto.controller.response.AnswerDto;
import com.quizzes.api.common.dto.controller.response.ChoiceDto;
import com.quizzes.api.common.dto.controller.response.CollectionDataDto;
import com.quizzes.api.common.dto.controller.response.CollectionDataResourceDto;
import com.quizzes.api.common.dto.controller.response.InteractionDto;
import com.quizzes.api.common.dto.controller.response.QuestionDataDto;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Resource;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        CollectionDataResourceDto resource1 = new CollectionDataResourceDto();
        UUID resourceId1 = UUID.randomUUID();
        resource1.setId(resourceId1);
        resource1.setSequence(1);
        resource1.setIsResource(false);
        resource1.setQuestions(map);

        CollectionDataResourceDto resource2 = new CollectionDataResourceDto();
        UUID resourceId2 = UUID.randomUUID();
        resource2.setId(resourceId2);
        resource2.setIsResource(false);
        resource2.setSequence(2);
        resource2.setQuestions(map);

        List<CollectionDataResourceDto> resources = new ArrayList<>();
        resources.add(resource1);
        resources.add(resource2);

        CollectionDataDto collectionDto = new CollectionDataDto();
        UUID collectionId = UUID.randomUUID();
        collectionDto.setId(collectionId);
        collectionDto.setIsCollection(false);
        collectionDto.setResources(resources);

        when(collectionService.getCollection(any(UUID.class))).thenReturn(collectionDto);

        ResponseEntity<CollectionDataDto> result =
                collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());

        verify(collectionService, times(1)).getCollection(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        CollectionDataDto response = result.getBody();
        assertNotNull("Response Body is Null", response);
        assertFalse("IsCollection is true", response.getIsCollection());
        assertEquals("Wrong size in resources", 2, response.getResources().size());

        CollectionDataResourceDto responseResoource = response.getResources().get(1);
        assertEquals("Wrong size in resources", 2, responseResoource.getSequence());
        assertEquals("Wrong id for resource 2", resourceId2, responseResoource.getId());
        assertFalse("Wrong id for resource 2", responseResoource.getIsResource());
        assertSame(result.getBody().getClass(), CollectionDataDto.class);
    }

    @Test
    public void getCollectionNull() throws Exception {
        when(collectionService.getCollection(any(UUID.class))).thenReturn(null);

        ResponseEntity<CollectionDataDto> result =
                collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());

        verify(collectionService, times(1)).getCollection(any(UUID.class));

        assertNotNull("Response is not Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_FOUND, result.getStatusCode());
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