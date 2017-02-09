package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.mappers.EntityMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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

    @Autowired
    private EntityMapper entityMapper;

    @ApiOperation(
            value = "Creates Assignment",
            notes = "Creates an Assignment of a Collection or Assessment to specified Context," +
                    " returning a generated Context ID.")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the Context ID", response = IdResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/contexts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createContext(@ApiParam(name = "Body", value = "The contexts's collection ID, " +
            "class ID (optional) and the context data", required = true)
                                           @RequestBody ContextPostRequestDto contextPostRequestDto,
                                           @RequestAttribute(value = "profileId") UUID profileId,
                                           @RequestAttribute(value = "token") String token) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ContextPostRequestDto>> constraintViolations = validator.validate(contextPostRequestDto);

        if (!constraintViolations.isEmpty()) {
            List<String> constraintErrors = constraintViolations
                    .stream().map(violation -> String.format("Error in %s: %s", violation.getPropertyPath(),
                            violation.getMessage())).collect(Collectors.toList());
            Map<String, Object> errors = new HashMap<>();
            errors.put("Errors", constraintErrors);
            return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);
        }

        UUID contextId = contextService.createContext(contextPostRequestDto, profileId, token);
        return new ResponseEntity<>(new IdResponseDto(contextId), HttpStatus.OK);
    }

    @ApiOperation(value = "Gets Created Contexts",
            notes = "Gets all the Contexts created by a Profile.\n\n" +
                    "The session token should correspond to the Owner (Profile). " +
                    "The fields `profileId` and `hasStarted` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetResponseDto.class),
            @ApiResponse(code = 404, message = "Content Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/contexts/created", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetResponseDto>> getCreatedContexts(
            @RequestAttribute(value = "profileId") UUID profileId) throws Exception {
        List<ContextEntity> contexts = contextService.findCreatedContexts(profileId);
        return new ResponseEntity<>(
                contexts.stream().map(context -> entityMapper.mapContextEntityToContextGetResponseDto(context))
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @ApiOperation(
            value = "Gets Created Context by ID",
            notes = "Gets a Context by its Context ID created by a Profile.\n\n" +
                    "The session token should correspond to the Owner (Profile). " +
                    "The fields `profileId` and `hasStarted` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 404, message = "Content Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/contexts/{contextId}/created", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getCreatedContext(
            @ApiParam(name = "ContextID", required = true,
                    value = "The ID of the context you want to get from the set of created contexts.")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") UUID profileId) throws Exception {
        return new ResponseEntity<>(
                entityMapper.mapContextEntityToContextGetResponseDto(
                        contextService.findCreatedContext(contextId, profileId))
                , HttpStatus.OK);
    }

    @ApiOperation(value = "Gets Assigned Contexts",
            notes = "Gets all `Active` Contexts assigned to a Profile. " +
                    "The session token should correspond to the Assignee (Profile). " +
                    "Anonymous session token will be rejected by this endpoint.\n\n" +
                    "The fields `isActive` and `modifiedDate` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful Response Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 404, message = "Content Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/contexts/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetResponseDto>> getAssignedContexts(
            @RequestAttribute(value = "profileId") UUID profileId) throws Exception {
        List<AssignedContextEntity> contexts = contextService.findAssignedContexts(profileId);
        return new ResponseEntity<>(
                contexts.stream().map(context -> entityMapper.mapAssignedContextEntityToContextGetResponseDto(context))
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Gets Assigned Context by ID",
            notes = "Gets the `Active` Context specified by the Context ID assigned to a Profile. " +
                    "The session token should correspond to the Assignee (Profile). " +
                    "Anonymous session token will be rejected by this endpoint.\n\n" +
                    "The fields `isActive` and `modifiedDate` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 404, message = "Content Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(path = "/contexts/{contextId}/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getAssignedContext(
            @ApiParam(name = "ContextID", required = true,
                    value = "The ID of the context you want to get from the set of assigned contexts.")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") UUID profileId) throws Exception {
        return new ResponseEntity<>(
                entityMapper.mapAssignedContextEntityToContextGetResponseDto(
                        contextService.findAssignedContext(contextId, profileId))
                , HttpStatus.OK);
    }

    // TODO We need to clarify how will be integrated the Update for Contexts in Nile
    /*
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
    */

}
