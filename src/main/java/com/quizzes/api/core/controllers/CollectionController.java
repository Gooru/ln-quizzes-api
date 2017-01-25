package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.CollectionGetResponseDto;
import com.quizzes.api.core.services.CollectionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;


    @ApiOperation(value = "Get a collection by its collection ID",
            notes = "Gets Collection data, including Resources and Answers (in case of Question).")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the collection information",
                    response = CollectionGetResponseDto.class),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/v1/collection/{collectionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionGetResponseDto> getCollection(@PathVariable UUID collectionId,
                                                                  @RequestHeader(value = "lms-id",
                                                                          defaultValue = "quizzes") String lmsId,
                                                                  @RequestHeader(value = "profile-id") UUID profileId) {
        return new ResponseEntity<>(collectionService.findCollectionById(collectionId), HttpStatus.OK);
    }


}
