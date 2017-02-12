package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.services.content.CollectionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class CollectionController {

    private static final String COLLECTION_TYPE = "collection";
    private static final String ASSESSMENT_TYPE = "assessment";

    @Autowired
    private CollectionService collectionService;

    @ApiOperation(value = "Get collection",
            notes = "Gets Collection data, including Resources and Answers (in case of Question).")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the collection information", response = CollectionDto.class),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(
            path = "/collections/{collectionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionDto> getCollection(
            @ApiParam(value = "Collection ID", required = true, name = "Collection ID")
            @PathVariable UUID collectionId,
            @ApiParam(value = "Collection type. Valid types: 'collection' or 'assessment'",
                    required = true, name = "Type")
            @RequestParam String type,
            @RequestParam(required = false) boolean refresh) {

        return prepareResponse(getCollectionDto(collectionId, type, refresh));
    }

    private CollectionDto getCollectionDto(UUID collectionId, String type, boolean refresh) {
        if (COLLECTION_TYPE.equalsIgnoreCase(type)) {
            return refresh ?
                    collectionService.getCollectionWithCacheRefresh(collectionId) :
                    collectionService.getCollection(collectionId);
        } else if (ASSESSMENT_TYPE.equalsIgnoreCase(type)) {
            return refresh ?
                    collectionService.getAssessmentWithCacheRefresh(collectionId) :
                    collectionService.getAssessment(collectionId);
        } else {
            throw new InvalidRequestException("Invalid 'type' parameter: " + type);
        }
    }

    private ResponseEntity<CollectionDto> prepareResponse(CollectionDto collectionDto) {
        collectionDto.setOwnerId(null);
        if (collectionDto.getResources() != null && !collectionDto.getResources().isEmpty()) {
            collectionDto.getResources().forEach(resourceDto -> resourceDto.getMetadata().setCorrectAnswer(null));
        }
        return new ResponseEntity<>(collectionDto, HttpStatus.OK);
    }

}
