package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.response.CollectionDataDto;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.realtime.model.CollectionOnAir;
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

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class CollectionController extends AbstractRealTimeController {

    @Autowired
    private CollectionService collectionService;

    @ApiOperation(value = "Find collection on air by class and collection", notes = "Find collection on air by class and collection")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/onair",
                    method=RequestMethod.GET,
                    produces={"application/json;charset=utf8"})
    public CollectionOnAir findCollectionOnAir(@PathVariable String classId,
                                               @PathVariable String collectionId,
                                               HttpServletResponse response) throws Exception {
        CollectionOnAir collectionOnAir = collectionService.findCollectionOnAir(classId, collectionId);
        if (Objects.isNull(collectionOnAir)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return collectionOnAir;
    }

    @ApiOperation(value ="Get a collection by its collection ID",
                    notes = "Gets Collection data, including Resources and Answers (in case of Question).")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Returns the collection information", response = CollectionDataDto.class),
        @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(path = "/v1/collection/{collectionId}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionDataDto> getCollection(@PathVariable UUID collectionId,
                                                           @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                                           @RequestHeader(value = "profile-id") UUID profileId) {

        CollectionDataDto result = collectionService.getCollection(collectionId);

        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Find collections on air by class", notes = "Find collections on air by class")
    @RequestMapping(path="/class/{classId}/onair",
                    method=RequestMethod.GET,
                    produces={"application/json;charset=utf8"})
    public Iterable<CollectionOnAir> findCollectionsOnAir(@PathVariable String classId) throws Exception {
        return collectionService.findCollectionsOnAirByClass(classId);
    }

    @ApiOperation(value = "Add new collection on air", notes = "Add new collection on air")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/onair",
                    method=RequestMethod.POST)
    public void addCollectionOnAir(@PathVariable String classId,
                                   @PathVariable String collectionId) throws Exception {
        collectionService.addCollectionOnAir(classId, collectionId);
    }

    @ApiOperation(value = "Remove new collection on air", notes = "Remove new collection on air")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/onair",
                    method=RequestMethod.DELETE)
    public void removeCollectionOnAir(@PathVariable String classId,
                                      @PathVariable String collectionId) {
        collectionService.removeCollectionOnAir(classId, collectionId);
    }

    @ApiOperation(value = "Set collection on complete", notes = "Set collection on complete")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/user/{userId}/complete",
                    method=RequestMethod.POST)
    public void completeCollection(@PathVariable String classId,
                                   @PathVariable String collectionId,
                                   @PathVariable String userId) {
        String collectionUniqueId = buildCollectionUniqueId(classId, collectionId);
        collectionService.completeCollectionForUser(collectionUniqueId, userId);
    }

    @ApiOperation(value = "Reset collections on air by user", notes = "Reset collections on air by user")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/user/{userId}/reset",
                    method=RequestMethod.DELETE)
    public void resetCollection(@PathVariable String classId,
                                @PathVariable String collectionId,
                                @PathVariable String userId) {
        String collectionUniqueId = buildCollectionUniqueId(classId, collectionId);
        collectionService.resetCollectionForUser(collectionUniqueId, userId);
    }

}
