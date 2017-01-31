package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.services.content.AssessmentService;
import com.quizzes.api.core.services.content.CollectionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    AssessmentService assessmentService;


    @ApiOperation(value = "Get a collection by its collection ID",
            notes = "Gets Collection data, including Resources and Answers (in case of Question).")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the collection information",
                    response = CollectionGetResponseDto.class),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/collections/old/{collectionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionGetResponseDto> getCollectionOld(@PathVariable UUID collectionId) {
        return new ResponseEntity<>(collectionService.findCollectionById(collectionId), HttpStatus.OK);
    }

    @RequestMapping(
            path = "/collections/{collectionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionDto> getCollection(@PathVariable UUID collectionId) {
        return new ResponseEntity<>(assessmentService.getCollection(collectionId.toString()),HttpStatus.OK);
    }


}
