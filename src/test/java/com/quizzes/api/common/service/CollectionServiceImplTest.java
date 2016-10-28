package com.quizzes.api.common.service;

import com.google.common.collect.Lists;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    public void findByExternalIdAndLmsId() throws Exception {
        UUID id = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        Collection collection =
                new Collection(id, "external-id", Lms.its_learning, true, profileId, "body", false, false, null);
        doReturn(collection).when(collectionService).findByExternalIdAndLmsId("external-id", Lms.its_learning);

        Collection result = collectionService.findByExternalIdAndLmsId("external-id", Lms.its_learning);
        verify(collectionService, times(1))
                .findByExternalIdAndLmsId(Mockito.eq("external-id"), Mockito.eq(Lms.its_learning));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong lms id", Lms.its_learning, result.getLmsId());
        assertEquals("Wrong owner profile", profileId, result.getOwnerProfileId());
        assertEquals("Wrong collection data", "body", result.getCollectionData());
        assertTrue("isCollection is not true", result.getIsCollection());
        assertFalse("isLock is not false", result.getIsLock());
        assertFalse("isDeleted is not false", result.getIsDeleted());
        assertNull("createdAt is not null", result.getCreatedAt());
    }

    @Test
    public void save() throws Exception {
        UUID id = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        Collection collection =
                new Collection(null, "external-id", Lms.its_learning, true, profileId, "body", false, false, null);
        doReturn(collection).when(collectionService).save(collection);

        Collection result = collectionService.save(collection);
        collection.setId(id);
        verify(collectionService, times(1)).save(Mockito.eq(collection));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong lms id", Lms.its_learning, result.getLmsId());
        assertEquals("Wrong owner profile", profileId, result.getOwnerProfileId());
        assertEquals("Wrong collection data", "body", result.getCollectionData());
        assertTrue("isCollection is not true", result.getIsCollection());
        assertFalse("isLock is not false", result.getIsLock());
        assertFalse("isDeleted is not false", result.getIsDeleted());
        assertNull("createdAt is not null", result.getCreatedAt());
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
        assertEquals("Wrong class", "classId", mockCollection.getClassId());
        assertEquals("Wrong collection", "collectionId", mockCollection.getCollectionId());
        assertNotNull("Response is Null", result);
    }

    @Test
    public void addCollectionOnAirWhenNotExist() throws Exception {
        doReturn(null).when(collectionService).findCollectionOnAir("classId", "collectionId");
        when(collectionOnAirRepository.save(any(CollectionOnAir.class))).thenReturn(new CollectionOnAir("classId", "collectionId"));

        CollectionOnAir result = collectionService.addCollectionOnAir("classId", "collectionId");

        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(collectionOnAirRepository, times(1)).save(any(CollectionOnAir.class));
        assertEquals("Wrong class", "classId", result.getClassId());
        assertEquals("Wrong collection", "collectionId", result.getCollectionId());
        assertNotNull("Response is Null", result);
    }

    @Test
    public void addCollectionOnAir() throws Exception {
        CollectionOnAir mockCollection = new CollectionOnAir("classId", "collectionId");
        doReturn(mockCollection).when(collectionService).findCollectionOnAir("classId", "collectionId");

        CollectionOnAir result = collectionService.addCollectionOnAir("classId", "collectionId");

        verify(collectionService, times(1)).findCollectionOnAir(Mockito.eq("classId"), Mockito.eq("collectionId"));
        verify(collectionOnAirRepository, times(0)).save(any(CollectionOnAir.class));
        assertEquals("Wrong class", "classId", result.getClassId());
        assertEquals("Wrong collection", "collectionId", result.getCollectionId());
        assertNotNull("Response is Null", result);
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
        assertEquals("Wrong size in list", 2, resultList.size());

        assertEquals("Wrong collection in array", "firstCollection", resultList.get(0).getCollectionId());
        assertEquals("Wrong collection in array", "secondCollection", resultList.get(1).getCollectionId());

        assertNotEquals("Wrong collection in array", "secondCollection", resultList.get(0).getCollectionId());
        assertNotEquals("Wrong collection in array", "firstCollection", resultList.get(1).getCollectionId());

        assertNotNull("Response is Null", result);
    }


}