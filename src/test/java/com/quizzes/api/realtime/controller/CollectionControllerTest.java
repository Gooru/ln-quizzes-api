package com.quizzes.api.realtime.controller;

import com.google.common.collect.Lists;
import com.quizzes.api.common.dto.controller.response.CollectionDataDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
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
        Collection collection = new Collection();
        collection.setId(UUID.randomUUID());
        collection.setIsCollection(false);
        when(collectionService.findById(any(UUID.class))).thenReturn(collection);

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setId(UUID.randomUUID());
        resource1.setIsResource(true);
        resource1.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"SingleChoice\"," +
                "\"correctAnswer\": [{\"value\": \"A\"}],\"body\": \"mocked body\",\"interaction\":" +
                " {\"shuffle\": true,\"maxChoices\": 10,\"prompt\": \"mocked Interaction\",\"choices\":" +
                " [{\"text\": \"option 1\",\"isFixed\": false,\"value\": \"A\"},{\"text\": \"option 2\",\"isFixed\":" +
                " false,\"value\": \"B\"},{\"text\": \"option 3\",\"isFixed\": false,\"value\": \"C\"}]}}");
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setId(UUID.randomUUID());
        resource2.setIsResource(true);
        resource2.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"True/False\",\"correctAnswer\":" +
                " [{\"value\": \"T\"}],\"body\": \"mocked body\",\"interaction\": {\"shuffle\": true,\"maxChoices\":" +
                " 10,\"prompt\": \"mocked Interaction\",\"choices\": [{\"text\": \"True\",\"isFixed\": false,\"value\": " +
                "\"T\"},{\"text\": \"False\",\"isFixed\": false,\"value\": \"F\"}]}}");
        resources.add(resource2);
        when(resourceService.getResourcesByCollectionId(collection.getId())).thenReturn(resources);

        ResponseEntity<CollectionDataDTO> result = collectionController.getCollection(UUID.randomUUID(), Lms.quizzes.getLiteral(), UUID.randomUUID());
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