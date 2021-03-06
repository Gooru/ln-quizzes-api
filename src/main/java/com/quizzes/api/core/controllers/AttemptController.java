package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.AttemptGetResponseDto;
import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.services.AttemptService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private ContextProfileService contextProfileService;

    @ApiOperation(value = "Get Current Attempt Data Grouped By Profile",
            notes = "Returns the information of the Current (or Last) Attempt for every Profile ID assigned to " +
                    "the Context ID. The session-token should correspond to the owner of the Context ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ContextAttemptsResponseDto.class),
            @ApiResponse(code = 404, message = "Provided contextId does not exist"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/attempts/contexts/{contextId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextAttemptsResponseDto> getCurrentAttemptByProfile(
            @ApiParam(value = "Context ID", required = true, name = "contextId")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") String profileId,
            @RequestAttribute(value = "token") String token) {
        ContextAttemptsResponseDto contextEvents =
                attemptService.getCurrentAttemptByProfile(contextId, UUID.fromString(profileId), token);
        return new ResponseEntity<>(contextEvents, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all the student event attempts",
            notes = "Returns the list of student events attempts IDs")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = AttemptIdsResponseDto.class),
            @ApiResponse(code = 404, message = "Provided contextId does not exist"),
            @ApiResponse(code = 403, message = "Invalid assignee"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "attempts/contexts/{contextId}/profiles/{profileId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AttemptIdsResponseDto> getAttemptIds(
            @ApiParam(value = "Context ID", required = true, name = "contextId")
            @PathVariable UUID contextId,
            @ApiParam(value = "Assignee Profile ID", required = true, name = "profileId")
            @PathVariable(name = "profileId") String profileId,
            @RequestAttribute(value = "profileId") String authProfileId,
            @RequestAttribute(value="token") String token) {
        UUID resolvedProfileId = QuizzesUtils.resolveProfileId(profileId);
        UUID resolvedAuthProfileId = QuizzesUtils.resolveProfileId(authProfileId);
        if (!resolvedAuthProfileId.equals(resolvedProfileId)) {
            // Checks that exists a Context ID owned by the AuthProfile ID. This means that the Context owner will
            // fetch Assignee (ProfileId) attempt events
            contextService.findCreatedContext(contextId, resolvedAuthProfileId, token);
        }
        List<UUID> attemptIds =
                contextProfileService.findContextProfileIdsByContextIdAndProfileId(contextId, resolvedProfileId);
        AttemptIdsResponseDto attemptIdsDto = new AttemptIdsResponseDto();
        attemptIdsDto.setAttempts(attemptIds);
        return new ResponseEntity<>(attemptIdsDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Get the attempt information",
            notes = "Returns the information for a given student event attempt.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = AttemptGetResponseDto.class),
            @ApiResponse(code = 404, message = "Provided attempt does not exist"),
            @ApiResponse(code = 403, message = "Invalid profile"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/attempts/{attemptId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AttemptGetResponseDto> getAttempt(
            @ApiParam(value = "Event attempt ID", required = true, name = "attemptId")
            @PathVariable UUID attemptId,
            @RequestAttribute(value = "profileId") String profileId) {
        AttemptGetResponseDto attemptDto =
                attemptService.getAttempt(attemptId, QuizzesUtils.resolveProfileId(profileId));
        return new ResponseEntity<>(attemptDto, HttpStatus.OK);
    }
}
