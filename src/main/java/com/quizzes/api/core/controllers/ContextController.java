package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.*;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.services.ContextService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api/v1")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @ApiOperation(
            value = "Creates an assignment",
            notes = "Creates an assignment of a collection or an assessment to specified context," +
                    " returning a generated Context ID.")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the Context ID", response = IdResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/contexts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignContext(@ApiParam(name = "Body", value = "The contexts's collection ID, " +
            "class ID (optional) and the context data", required = true)
                                           @RequestBody ContextPostRequestDto contextPostRequestDto,
                                           @RequestAttribute(value = "profileId") String profileId,
                                           @RequestAttribute(value = "token") String token) {

        List<String> constraintErrors = new ArrayList<>();
        if (profileId == null) {
            constraintErrors.add("Error in profileId: profileId is required");
        }

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ContextPostRequestDto>> constraintViolations = validator.validate(contextPostRequestDto);

        if (!constraintViolations.isEmpty()) {
            constraintErrors.addAll(constraintViolations
                    .stream().map(violation -> String.format("Error in %s: %s", violation.getPropertyPath(),
                            violation.getMessage())).collect(Collectors.toList()));
        }

        if (!constraintErrors.isEmpty()) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("Errors", constraintErrors);
            return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);

        }
        IdResponseDto result = contextService.createContext(contextPostRequestDto, profileId, token);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Get created contexts",
            notes = "Get all the contexts created by the Owner Profile.\n\nThe fields `owner` and `hasStarted` won't be present on requests to this end point")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetResponseDto.class)
    })
    @RequestMapping(path = "/contexts/created",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetResponseDto>> getCreatedContexts(
            @ApiParam(name = "lms-id", required = false, value = "Client LMS ID")
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @ApiParam(name = "profile-id", required = true, value = "Context owner Profile ID")
            @RequestHeader(value = "profile-id") UUID profileId,
            @ApiParam(value = "optional query params", required = true, name = "filterMap")
            @RequestParam Map<String, String> filterMap) throws Exception {

        List<ContextGetResponseDto> list = contextService.findCreatedContexts(profileId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned contexts",
          notes = "Get all the ‘active’ contexts assigned to the assignee profile.\n\nThe fields `assignees` and `modifiedDate` won't be present on requests to this end point")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                  response = ContextGetResponseDto.class)
    })
    @RequestMapping(path = "/contexts/assigned",
          method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetResponseDto>> getAssignedContexts(
          @ApiParam(name = "lms-id", required = false, value = "Client LMS ID")
          @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
          @ApiParam(name = "profile-id", required = true, value = "Assignee Profile ID")
          @RequestHeader(value = "profile-id") UUID profileId,
          @ApiParam(value = "Filter the contexts by isActive flag", required = false, name = "isActive")
          @RequestParam(value = "isActive", required = false) Boolean isActive,
          @ApiParam(value = "Filter the contexts by start date in milliseconds", required = false, name = "startDate")
          @RequestParam(value = "startDate", required = false) Long startDate,
          @ApiParam(value = "Filter the contexts by due date in milliseconds", required = false, name = "dueDate")
          @RequestParam(value = "dueDate", required = false) Long dueDate) throws Exception {

        if (isActive != null && (startDate != null || dueDate != null)) {
            throw new InvalidRequestException("isActive parameter can't be combined with startDate or dueDate");
        }

        List<ContextGetResponseDto> contexts = contextService.getAssignedContexts(profileId, isActive, startDate, dueDate);
        return new ResponseEntity<>(contexts, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Get created context by ID",
            notes = "Gets a Context by the Context ID from the set of created contexts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class)
    })
    @RequestMapping(path = "/contexts/{contextId}/created",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getCreatedContextByContextId(
            @ApiParam(name = "ContextID", required = true, value = "The ID of the context you want to get from the set of created contexts.")
            @PathVariable UUID contextId,
            @ApiParam(name = "lms-id", required = false, value = "Client LMS ID")
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @ApiParam(name = "profile-id", required = true, value = "Context owner Profile ID")
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetResponseDto result = contextService.findCreatedContextByContextIdAndOwnerId(contextId, profileId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned context by ID",
            notes = "Gets a Context by the Context ID from the set of assigned contexts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class)
    })
    @RequestMapping(path = "/contexts/{contextId}/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getAssignedContextByContextId(
            @ApiParam(name = "ContextID", required = true, value = "The ID of the context you want to get from the set of assigned contexts.")
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {
        ContextGetResponseDto response =
                contextService.getAssignedContextByContextIdAndAssigneeId(contextId, profileId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Update context", notes = "Updates the context data and adds assignees to the context.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the Context ID", response = IdResponseDto.class),
    })
    @RequestMapping(path = "/contexts/{contextId}",
            method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDto> updateContext(
            @PathVariable UUID contextId,
            @ApiParam(name = "Body", required = true, value = "The Assignees to add and the context data to update")
            @RequestBody ContextPutRequestDto contextPutRequestDto,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @ApiParam(name = "profile-id", required = true, value = "Context's owner profile ID")
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        Context context = contextService.update(contextId, profileId, contextPutRequestDto);

        if (context == null || context.getId() == null) {
            throw new IllegalArgumentException("Error trying to get the updated context");
        }

        IdResponseDto result = new IdResponseDto();
        result.setId(context.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
