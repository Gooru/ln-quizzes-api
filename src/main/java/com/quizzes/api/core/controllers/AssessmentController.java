package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.services.content.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class AssessmentController {

    @Autowired
    AssessmentService assessmentService;

    @RequestMapping(
            path = "/assessments/{assessmentId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionDto> getAssessment(@PathVariable UUID assessmentId) {
        return new ResponseEntity<>(assessmentService.getAssessment(String.valueOf(assessmentId)),HttpStatus.OK);
    }

}
