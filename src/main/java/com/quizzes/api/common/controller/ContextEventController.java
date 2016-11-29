package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.service.ContextEventService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class ContextEventController {

    @Autowired
    private ContextEventService contextEventService;

    @ApiOperation(
            value = "Start collection",
            notes = "Sends event to start the Collection attempt associated to the context. " +
                    "If the Collection attempt was not started previously there is not a start action executed. " +
                    "In any case returns the current attempt status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = StartContextEventResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context/{contextId}/event/start",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartContextEventResponseDto> startContextEvent(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) {
        StartContextEventResponseDto result = contextEventService.startContextEvent(contextId, profileId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Finish collection",
            notes = "Sends event to finish the current collection attempt.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Finish the current attempt"),
            @ApiResponse(code = 500, message = "Bad request")
    })
    @RequestMapping(path = "/v1/context/{contextId}/event/end", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> finishContextEvent(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) {
        contextEventService.finishContextEvent(contextId, profileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Add event",
            notes = "Sends event to indicate current resource position and provides the data generated" +
                    " in the previous resource (this value could be null in case there is not previous resource)")
    @ApiResponses({@ApiResponse(code = 200, message = "OK")})
    @RequestMapping(path = "/v1/context/{contextId}/event/on-resource/{resourceId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addEvent(@PathVariable UUID resourceId,
                                         @PathVariable UUID contextId,
                                         @ApiParam(value = "Json body", required = true, name = "Body")
                                         @RequestBody OnResourceEventPostRequestDto onResourceEventPostRequestDto,
                                         @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                         @RequestHeader(value = "profile-id") UUID profileId) {
        contextEventService.addEvent(contextId, resourceId, profileId, onResourceEventPostRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

