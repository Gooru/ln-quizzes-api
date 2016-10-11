package com.quizzes.api.common.service;

import com.google.common.collect.Lists;
import com.quizzes.api.realtime.model.CollectionOnAir;
import com.quizzes.api.realtime.repository.CollectionOnAirRepository;
import com.quizzes.api.realtime.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionServiceImplTest {

    @InjectMocks
    private CollectionService collectionService = Mockito.spy(CollectionServiceImpl.class);

    @Mock
    private EventService eventService;

    @Mock
    private CollectionOnAirRepository collectionOnAirRepository;

    @Test
    public void getOrCreateCollection() throws Exception {

    }

    @Test
    public void removeCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        doReturn(mockCollection).when(collectionService).findCollectionOnAir("classId", "collectionId");

        collectionService.removeCollectionOnAir("classId", "collectionId");
        verify(collectionOnAirRepository, times(1)).delete(Mockito.eq(mockCollection));
    }

    @Test
    public void removeCollectionOnAirWhenNotExist() throws Exception {
        doReturn(null).when(collectionService).findCollectionOnAir("classId", "collectionId");

        collectionService.removeCollectionOnAir("classId", "collectionId");
        verify(collectionOnAirRepository, times(0)).delete(any(CollectionOnAir.class));
    }

    @Test
    public void completeCollectionForUser() throws Exception {
        collectionService.completeCollectionForUser("collectionUniqueId", "userId");
        verify(eventService, times(1)).completeEventIndexByUser(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
    }

    @Test
    public void resetCollectionForUser() throws Exception {
        collectionService.resetCollectionForUser("collectionUniqueId", "userId");
        verify(eventService, times(1)).deleteCollectionEventsByUser(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
    }

    @Test
    public void findCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        when(collectionOnAirRepository.findFirstByClassIdAndCollectionId("classId", "collectionId")).thenReturn(mockCollection);

        CollectionOnAir result = collectionService.findCollectionOnAir("classId", "collectionId");
        verify(collectionOnAirRepository, times(1)).findFirstByClassIdAndCollectionId(Mockito.eq("classId"), Mockito.eq("collectionId"));
        assertEquals(mockCollection.getClassId(), "classId");
        assertEquals(mockCollection.getCollectionId(), "collectionId");
        assertNotNull(result);
    }

    @Test
    public void addCollectionOnAirWhenNotExist() throws Exception {
        doReturn(null).when(collectionService).findCollectionOnAir("classId", "collectionId");
        when(collectionOnAirRepository.save(any(CollectionOnAir.class))).thenReturn(new CollectionOnAir("classId", "collectionId"));

        CollectionOnAir result = collectionService.addCollectionOnAir("classId", "collectionId");

        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(collectionOnAirRepository, times(1)).save(any(CollectionOnAir.class));
        assertEquals(result.getClassId(), "classId");
        assertEquals(result.getCollectionId(), "collectionId");
        assertNotNull(result);
    }

    @Test
    public void addCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        doReturn(mockCollection).when(collectionService).findCollectionOnAir("classId", "collectionId");

        CollectionOnAir result = collectionService.addCollectionOnAir("classId", "collectionId");

        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(collectionOnAirRepository, times(0)).save(any(CollectionOnAir.class));
        assertEquals(result.getClassId(), "classId");
        assertEquals(result.getCollectionId(), "collectionId");
        assertNotNull(result);
    }

    @Test
    public void findCollectionsOnAirByClass() throws Exception {
        List<CollectionOnAir> collections = new ArrayList<>();
        collections.add(new CollectionOnAir("classId", "firstCollection"));
        collections.add(new CollectionOnAir("classId", "secondCollection"));
        when(collectionOnAirRepository.findByClassId("classId")).thenReturn(collections);

        Iterable<CollectionOnAir> result = collectionService.findCollectionsOnAirByClass("classId");
        verify(collectionOnAirRepository, times(1)).findByClassId(Mockito.eq("classId"));

        // Creating the list to verify the size
        List<CollectionOnAir> resultList = Lists.newArrayList(result);
        assertEquals(resultList.size(), 2);

        assertEquals(resultList.get(0).getCollectionId(), "firstCollection");
        assertEquals(resultList.get(1).getCollectionId(), "secondCollection");

        assertNotEquals(resultList.get(0).getCollectionId(), "secondCollection");
        assertNotEquals(resultList.get(1).getCollectionId(), "firstCollection");

        assertNotNull(result);
    }


}