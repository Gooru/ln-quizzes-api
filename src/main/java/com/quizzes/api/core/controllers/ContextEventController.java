package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.services.ContextEventService;
import com.quizzes.api.core.services.ContextProfileService;
import com.quizzes.api.core.services.ContextService;
import com.quizzes.api.util.QuizzesUtils;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class ContextEventController {

    @Autowired
    private ContextEventService contextEventService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private ContextProfileService contextProfileService;

    @ApiOperation(
            value = "Start collection attempt",
            notes = "Sends event to start the Collection attempt associated to the context. " +
                    "If the Collection attempt was not started previously there is not a start action executed. " +
                    "In any case returns the current attempt status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = StartContextEventResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/contexts/{contextId}/start", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartContextEventResponseDto> startContextEvent(
            @ApiParam(value = "Id of the context that will be started", required = true, name = "ContextID")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") UUID profileId) {
        return new ResponseEntity<>(contextEventService.processStartContextEvent(contextId, profileId), HttpStatus.OK);
    }

    @ApiOperation(value = "On resource event",
            notes = "Sends event to indicate current resource position and provides the data generated" +
                    " in the previous resource (this value could be null in case there is not previous resource)")
    @ApiResponses({
            @ApiResponse(code = 204, message = "No Content")
    })
    @RequestMapping(path = "/contexts/{contextId}/onResource/{resourceId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> onResourceEvent(@ApiParam(value = "ID of the resource that we are sending data to", required = true, name = "Resource ID")
                                                @PathVariable UUID resourceId,
                                                @ApiParam(value = "ID of the context that the resource belongs to", required = true, name = "Context ID")
                                                @PathVariable UUID contextId,
                                                @ApiParam(value = "Json body containing data to send to the requested event endpoint.", required = true, name = "Body")
                                                @RequestBody OnResourceEventPostRequestDto onResourceEventPostRequestDto,
                                                @RequestAttribute(value = "profileId") UUID profileId) {
        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, onResourceEventPostRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "Finish collection attempt",
            notes = "Sends event to finish the current collection attempt.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Finish the current attempt"),
            @ApiResponse(code = 500, message = "Bad request")
    })
    @RequestMapping(path = "/contexts/{contextId}/finish", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> finishContextEvent(
            @ApiParam(value = "ID of the context to have its attempt finished.", required = true, name = "ContextID")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") UUID profileId,
            @RequestAttribute(value = "token") String token) {
        contextEventService.processFinishContextEvent(contextId, profileId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

