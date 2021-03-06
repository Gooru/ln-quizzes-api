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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

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
    public void getAssessmentWithCacheRefresh() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doReturn(collectionDto).when(collectionRestClient).getAssessmentWithCacheRefresh(any(UUID.class), anyString());

        CollectionDto result = collectionService.getAssessment(assessmentId, true, "token");

        verify(collectionRestClient, times(1)).getAssessmentWithCacheRefresh(any(UUID.class), anyString());
        verifyPrivate(collectionService, times(1)).invoke("getQuestionRubrics", result, "token");
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionWithCacheRefresh() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionRestClient).getCollectionWithCacheRefresh(any(UUID.class), anyString());

        CollectionDto result = collectionService.getCollection(collectionId, true, "token");

        verify(collectionRestClient, times(1)).getCollectionWithCacheRefresh(any(UUID.class), anyString());
        assertEquals("Wrong Collection ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getAssessmentWithNoCacheRefresh() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doReturn(collectionDto).when(collectionRestClient).getAssessment(any(UUID.class), anyString());

        CollectionDto result = collectionService.getAssessment(assessmentId, "token");

        verify(collectionService, times(1)).getAssessment(any(UUID.class), eq(false), anyString());
        verify(collectionRestClient, times(1)).getAssessment(any(UUID.class), anyString());
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionWithNoCacheRefresh() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionRestClient).getCollection(any(UUID.class), anyString());

        CollectionDto result = collectionService.getCollection(collectionId, "token");

        verify(collectionService, times(1)).getCollection(any(UUID.class), anyString());
        verify(collectionRestClient, times(1)).getCollection(any(UUID.class), anyString());
        assertEquals("Wrong Assessment ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionOrAssessmentForValidCollectionId() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class), anyString());

        CollectionDto result = collectionService.getCollectionOrAssessment(collectionId, "token");

        verify(collectionService, times(1)).getCollection(any(UUID.class), anyString());
        verify(collectionService, times(0)).getAssessment(any(UUID.class), anyString());
        assertEquals("Wrong Assessment ID", collectionId.toString(), result.getId());
        assertTrue("Wrong IsCollection value", result.getIsCollection());
    }

    @Test
    public void getCollectionOrAssessmentForValidAssessmentId() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(false);

        doThrow(ContentNotFoundException.class).when(collectionService).getCollection(any(UUID.class), anyString());
        doReturn(collectionDto).when(collectionService).getAssessment(any(UUID.class), anyString());

        CollectionDto result = collectionService.getCollectionOrAssessment(assessmentId, "token");

        verify(collectionService, times(1)).getCollection(any(UUID.class), anyString());
        verify(collectionService, times(1)).getAssessment(any(UUID.class), anyString());
        assertEquals("Wrong Assessment ID", assessmentId.toString(), result.getId());
        assertFalse("Wrong IsCollection value", result.getIsCollection());
    }

    @Test(expected = ContentNotFoundException.class)
    public void getCollectionOrAssessmentForInvalidCollectionAndAssessmentId() {
        doThrow(ContentNotFoundException.class).when(collectionService).getCollection(any(UUID.class), anyString());
        doThrow(ContentNotFoundException.class).when(collectionService).getAssessment(any(UUID.class), anyString());

        collectionService.getCollectionOrAssessment(assessmentId, "token");
    }

    @Test
    public void getAssessmentQuestions() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId.toString());
        collectionDto.setIsCollection(true);
        List<ResourceDto> resourceDtos = new ArrayList<>();
        resourceDtos.add(new ResourceDto());
        collectionDto.setResources(resourceDtos);

        doReturn(collectionDto).when(collectionService).getAssessment(any(UUID.class), anyString());

        List<ResourceDto> result = collectionService.getAssessmentQuestions(assessmentId, "token");

        verify(collectionService, times(1)).getAssessment(any(UUID.class), anyString());
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

        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class), anyString());

        List<ResourceDto> result = collectionService.getCollectionResources(collectionId, "token");

        verify(collectionService, times(1)).getCollection(any(UUID.class), anyString());
        assertEquals("Wrong Resources list size", 1, result.size());
    }

}