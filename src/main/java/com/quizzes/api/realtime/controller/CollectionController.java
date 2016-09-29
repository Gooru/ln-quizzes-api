package com.quizzes.api.realtime.controller;

import io.swagger.annotations.ApiOperation;
import com.quizzes.api.realtime.model.CollectionOnAir;
import com.quizzes.api.realtime.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


@CrossOrigin
@RestController
@RequestMapping("/nucleus/realtime")
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
