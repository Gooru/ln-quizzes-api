package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ResourceEventDto;
import com.quizzes.api.core.services.ResourceEventService;
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
public class ResourceController {

    @Autowired
    private ResourceEventService resourceEventService;

    @ApiOperation(
            value = "Single resource finish event",
            notes = "Sends an event directly to Analytics API with Time Spent and Reaction data.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Event was sent OK and no result value expected."),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @RequestMapping(path = "/resources/{resourceId}/finish", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> finishResourceEvent(
            @ApiParam(value = "Resource ID.", required = true, name = "ResourceID")
            @PathVariable UUID resourceId,
            @ApiParam(value = "Request body in JSON format containing the event data.", required = true,
                    name = "Request Body")
            @RequestBody ResourceEventDto resourceEventDto,
            @RequestAttribute(value = "profileId") String profileId,
            @RequestAttribute(value = "token") String token) {
        UUID resolvedProfileId = QuizzesUtils.resolveProfileId(profileId);
        resourceEventDto.getResourceEventData().setResourceId(resourceId);
        resourceEventService.processFinishResourceEvent(resourceEventDto, resolvedProfileId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
