package com.quizzes.api.realtime.controller;

import com.google.common.collect.Lists;
import com.quizzes.api.common.service.CollectionServiceImpl;
import com.quizzes.api.realtime.model.CollectionOnAir;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionControllerTest {
    @InjectMocks
    private CollectionController collectionController = new CollectionController();

    @Mock
    private CollectionServiceImpl collectionServiceImpl;

    HttpServletResponse response = mock(HttpServletResponse.class);

    @Test
    public void findCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        when(collectionServiceImpl.findCollectionOnAir("classId", "collectionId")).thenReturn(mockCollection);

        CollectionOnAir result = collectionController.findCollectionOnAir("classId", "collectionId", response);
        verify(collectionServiceImpl, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        assertNotNull(result);
        assertEquals(mockCollection.getCollectionId(), "collectionId");
        assertEquals(mockCollection.getClassId(), "classId");
    }

    @Test
    public void findCollectionOnAirNotFound() throws Exception {
        when(collectionServiceImpl.findCollectionOnAir("classId", "collectionId")).thenReturn(null);

        CollectionOnAir result = collectionController.findCollectionOnAir("classId", "collectionId", response);
        verify(collectionServiceImpl, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(response, times(1)).setStatus(HttpStatus.NOT_FOUND.value());
        assertNull(result);
    }

    @Test
    public void findCollectionsOnAir() throws Exception {
        List<CollectionOnAir> collections = new ArrayList<>();
        collections.add(new CollectionOnAir("classId", "firstCollection"));
        collections.add(new CollectionOnAir("classId", "secondCollection"));
        when(collectionServiceImpl.findCollectionsOnAirByClass("classId")).thenReturn(collections);

        Iterable<CollectionOnAir> result = collectionController.findCollectionsOnAir("classId");
        verify(collectionServiceImpl, times(1)).findCollectionsOnAirByClass(Mockito.eq("classId"));

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
        verify(collectionServiceImpl, times(1)).addCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
    }

    @Test
    public void removeCollectionOnAir() throws Exception {
        collectionController.removeCollectionOnAir("classId", "collectionId");
        verify(collectionServiceImpl, times(1)).removeCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
    }

    @Test
    public void completeCollection() throws Exception {
        collectionController.completeCollection("classId", "collectionId", "userId");
        verify(collectionServiceImpl, times(1)).completeCollectionForUser(Mockito.eq("classId_collectionId"), Mockito.eq("userId"));
    }

    @Test
    public void resetCollection() throws Exception {
        collectionController.resetCollection("classId", "collectionId", "userId");
        verify(collectionServiceImpl, times(1)).resetCollectionForUser(Mockito.eq("classId_collectionId"), Mockito.eq("userId"));
    }

}