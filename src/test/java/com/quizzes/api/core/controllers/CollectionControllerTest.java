package com.quizzes.api.core.controllers;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.services.content.AssessmentService;
import com.quizzes.api.core.services.content.CollectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
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
    private AssessmentService assessmentService;

    @Mock
    Gson gson = new Gson();

    @Test
    public void getCollection() throws Exception {
        String collectionId = String.valueOf(UUID.randomUUID());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId);

        PowerMockito.when(assessmentService.getCollection(collectionId)).thenReturn(collectionDto);

        ResponseEntity<CollectionDto> result = collectionController.getCollection(UUID.fromString(collectionId));

        verify(assessmentService, times(1)).getCollection(collectionId);
        assertEquals("Wrong status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong collection ID", collectionId, result.getBody().getId());
    }

}