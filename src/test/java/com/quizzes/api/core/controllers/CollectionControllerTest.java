package com.quizzes.api.core.controllers;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.services.content.CollectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class CollectionControllerTest {
    @InjectMocks
    private CollectionController collectionController;

    @Mock
    private CollectionService collectionService;

    @Mock
    private Gson gson = new Gson();

    private String collectionId;

    @Before
    public void before() {
        collectionId = UUID.randomUUID().toString();
    }

    @Test
    public void getCollectionWithTypeCollection() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId);

        PowerMockito.when(collectionService.getCollection(collectionId)).thenReturn(collectionDto);

        ResponseEntity<CollectionDto> result =
                collectionController.getCollection(UUID.fromString(collectionId), "collection");

        verify(collectionService, times(1)).getCollection(collectionId);
        assertEquals("Wrong status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong collection ID", collectionId, result.getBody().getId());
    }

    @Test
    public void getCollectionWithTypeAssessment() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId);

        when(collectionService.getAssessment(collectionId)).thenReturn(collectionDto);

        ResponseEntity<CollectionDto> result =
                collectionController.getCollection(UUID.fromString(collectionId), "assessment");

        verify(collectionService, times(1)).getAssessment(collectionId);
        assertEquals("Wrong status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong assessment ID", collectionId, result.getBody().getId());
    }

    @Test(expected = InvalidRequestException.class)
    public void getCollectionThrowException() throws Exception {
        collectionController.getCollection(UUID.fromString(collectionId), "as");
    }

}