package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.services.content.AssessmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class AssessmentControllerTest {

    @InjectMocks
    private AssessmentController assessmentController;

    @Mock
    private AssessmentService assessmentService;

    @Test
    public void getAssessment() throws Exception {
        String assessmentId = String.valueOf(UUID.randomUUID());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId);

        when(assessmentService.getAssessment(assessmentId)).thenReturn(collectionDto);

        ResponseEntity<CollectionDto> result = assessmentController.getAssessment(UUID.fromString(assessmentId));

        verify(assessmentService, times(1)).getAssessment(assessmentId);
        assertEquals("Wrong status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong assessment ID", assessmentId, result.getBody().getId());
    }

}