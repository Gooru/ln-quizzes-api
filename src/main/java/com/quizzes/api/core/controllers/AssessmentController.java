package com.quizzes.api.core.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class AssessmentController {


    @RequestMapping(
            path = "/assessments/{assessmentId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getAssessment(@PathVariable UUID assessmentId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
