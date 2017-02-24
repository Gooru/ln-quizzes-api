package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CollectionService.class)
public class CollectionServiceTest {

    @InjectMocks
    private CollectionService collectionService = spy(new CollectionService());

    @Mock
    private CollectionRestClient collectionRestClient;

    private UUID assessmentId;
    private UUID collectionId;

    @Before
    public void before() throws Exception {
        assessmentId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
    }

    @Test
    public void getAssessment() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doReturn(collectionDto).when(collectionRestClient).getAssessment(any(UUID.class));

        CollectionDto result = collectionService.getAssessment(assessmentId);

        verify(collectionRestClient, times(1)).getAssessment(assessmentId);
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollection() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionRestClient).getCollection(any(UUID.class));

        CollectionDto result = collectionService.getCollection(collectionId);

        verify(collectionRestClient, times(1)).getCollection(collectionId);
        assertEquals("Wrong Collection ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getAssessmentWithCacheRefresh() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doReturn(collectionDto).when(collectionService).getAssessment(any(UUID.class));

        CollectionDto result = collectionService.getAssessmentWithCacheRefresh(assessmentId);

        verify(collectionService, times(1)).getAssessment(any(UUID.class));
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionWithCacheRefresh() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class));

        CollectionDto result = collectionService.getCollectionWithCacheRefresh(collectionId);

        verify(collectionService, times(1)).getCollection(any(UUID.class));
        assertEquals("Wrong Assessment ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionOrAssessmentForValidCollectionId() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class));

        CollectionDto result = collectionService.getCollectionOrAssessment(collectionId);

        verify(collectionService, times(1)).getCollection(any(UUID.class));
        verify(collectionService, times(0)).getAssessment(any(UUID.class));
        assertEquals("Wrong Assessment ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionOrAssessmentForValidAssessmentId() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doThrow(ContentNotFoundException.class).when(collectionService).getCollection(any(UUID.class));
        doReturn(collectionDto).when(collectionService).getAssessment(any(UUID.class));

        CollectionDto result = collectionService.getCollectionOrAssessment(assessmentId);

        verify(collectionService, times(1)).getCollection(any(UUID.class));
        verify(collectionService, times(1)).getAssessment(any(UUID.class));
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test(expected = ContentNotFoundException.class)
    public void getCollectionOrAssessmentForInvalidCollectionAndAssessmentId() {
        doThrow(ContentNotFoundException.class).when(collectionService).getCollection(any(UUID.class));
        doThrow(ContentNotFoundException.class).when(collectionService).getAssessment(any(UUID.class));

        collectionService.getCollectionOrAssessment(assessmentId);
    }

    @Test
    public void getAssessmentQuestions() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(true);
        List<ResourceDto> resourceDtos = new ArrayList<>();
        resourceDtos.add(new ResourceDto());
        collectionDto.setResources(resourceDtos);

        doReturn(collectionDto).when(collectionService).getAssessment(any(UUID.class));

        List<ResourceDto> result = collectionService.getAssessmentQuestions(assessmentId);

        verify(collectionService, times(1)).getAssessment(any(UUID.class));
        assertEquals("Wrong Questions list size", 1, result.size());
    }

    @Test
    public void getCollectionResources() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        List<ResourceDto> resourceDtos = new ArrayList<>();
        resourceDtos.add(new ResourceDto());
        collectionDto.setResources(resourceDtos);

        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class));

        List<ResourceDto> result = collectionService.getCollectionResources(collectionId);

        verify(collectionService, times(1)).getCollection(any(UUID.class));
        assertEquals("Wrong Resources list size", 1, result.size());
    }

}