package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.response.AnswerDTO;
import com.quizzes.api.common.dto.controller.response.ChoiceDTO;
import com.quizzes.api.common.dto.controller.response.CollectionDataDTO;
import com.quizzes.api.common.dto.controller.response.CollectionDataResourceDTO;
import com.quizzes.api.common.dto.controller.response.InteractionDTO;
import com.quizzes.api.common.dto.controller.response.QuestionDataDTO;
import com.quizzes.api.common.dto.controller.response.QuestionType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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

    @ApiOperation(value ="Get a collection by it's collection ID",
                    notes = "Gets Collection data, including Resources and Answers (in case of Question).")
    @ApiResponses(@ApiResponse(code = 200, message = "", response = CollectionDataDTO.class))
    @RequestMapping(path = "/v1/collection/{collectionId}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionDataDTO> getCollection(@PathVariable UUID collectionId) throws Exception {

        ChoiceDTO choiceDTO = new ChoiceDTO("mocked text", false, "mocked value");
        List<ChoiceDTO> choiceDTOList = new ArrayList<>();
        choiceDTOList.add(choiceDTO);
        InteractionDTO interactionDTO = new InteractionDTO(true, 10, "mocked Interaction", choiceDTOList);
        AnswerDTO answerDTO = new AnswerDTO("1");
        List<AnswerDTO>  answerDTOList = new ArrayList<>();
        answerDTOList.add(answerDTO);
        QuestionDataDTO questionDataDTO = new QuestionDataDTO("mocked Question Data", QuestionType.SingleChoice, answerDTOList, "mocked body", interactionDTO);
        CollectionDataResourceDTO resourceDTO = new CollectionDataResourceDTO(UUID.randomUUID(), true, questionDataDTO);
        List<CollectionDataResourceDTO> resources = new ArrayList<>();
        resources.add(resourceDTO);
        CollectionDataDTO result = new CollectionDataDTO(UUID.randomUUID(), true, resources);

        return new ResponseEntity(result, HttpStatus.OK);
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
