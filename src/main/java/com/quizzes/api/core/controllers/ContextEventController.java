package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextEventsResponseDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
                                                @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                                @RequestHeader(value = "profile-id") UUID profileId) {
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

    @ApiOperation(value = "Get All Student Events by Context ID",
            notes = "Returns the whole list of student events assigned to for the provided Context ID. The profile-id " +
                    "passed in the request header corresponds to the context owner Profile ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ContextEventsResponseDto.class),
            @ApiResponse(code = 404, message = "Provided contextId does not exist"),
            @ApiResponse(code = 403, message = "Invalid owner"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/context/{contextId}/events",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextEventsResponseDto> getContextEvents(
            @ApiParam(value = "Context ID", required = true, name = "contextId")
            @PathVariable UUID contextId,
            @ApiParam(value = "Client LMS ID", required = false, name = "lms-id")
            @RequestHeader(value = "client-id", defaultValue = "quizzes") String lmsId,
            @ApiParam(value = "Context owner Profile ID", required = true, name = "profile-id")
            @RequestHeader(value = "profile-id") UUID profileId) {
        ContextEventsResponseDto contextEvents = contextEventService.getContextEvents(contextId, profileId);
        return new ResponseEntity<>(contextEvents, HttpStatus.OK);
    }

    @ApiOperation(value = "Get the Student Events by Context ID",
            notes = "Returns the list of student events in the provided Context ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ContextEventsResponseDto.class),
            @ApiResponse(code = 404, message = "Provided contextId does not exist"),
            @ApiResponse(code = 403, message = "Invalid owner"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/context/{contextId}/events/assigned",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextEventsResponseDto> getContextEvents(
            @ApiParam(value = "Context ID", required = true, name = "contextId")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") String profileId) {

        if (profileId.equals("anonymous")) {
            throw new InvalidAssigneeException("anonymous users not allowed");
        }
        ContextEventsResponseDto contextEvents = contextEventService.getContextEventsAssigned(contextId, UUID.fromString(profileId));
        return new ResponseEntity<>(contextEvents, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all the student event attempts",
            notes = "Returns the list of student events attempts IDs")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ContextEventsResponseDto.class),
            @ApiResponse(code = 404, message = "Provided contextId does not exist"),
            @ApiResponse(code = 403, message = "Invalid assignee"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "contexts/{contextId}/profiles/{profileId}/attempts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AttemptIdsResponseDto> getContextProfileAttempIds(
            @ApiParam(value = "Context ID", required = true, name = "contextId")
            @PathVariable UUID contextId,
            @ApiParam(value = "Assignee Profile ID", required = true, name = "profileId")
            @PathVariable(name = "profileId") UUID assigneeProfileId,
            @RequestAttribute(value = "profileId") String authorizationProfileId) {
        QuizzesUtils.rejectAnonymous(authorizationProfileId);
        UUID authorizationProfileUUID = UUID.fromString(authorizationProfileId);
        if (assigneeProfileId != authorizationProfileUUID) {
            //this means that an authorized user is requesting for an assignee attempts
            //we need to verify that this user is the owner of the context
            contextService.findCreatedContext(contextId, authorizationProfileUUID);
        }

        AttemptIdsResponseDto attemptIdsDto = contextProfileService.findContextProfileAttemptIds(contextId, assigneeProfileId);
        return new ResponseEntity<>(attemptIdsDto, HttpStatus.OK);
    }
}

