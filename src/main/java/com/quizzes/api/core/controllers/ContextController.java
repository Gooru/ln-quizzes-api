package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.exceptions.InvalidRequestBodyException;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.mappers.EntityMapper;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
            @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(path = "/contexts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createContext(@ApiParam(name = "Body", value = "The contexts's collection ID, " +
            "class ID (optional) and the context data", required = true)
                                           @RequestBody ContextPostRequestDto contextPostRequestDto,
                                           @RequestAttribute(value = "profileId") String profileId,
                                           @RequestAttribute(value = "token") String token) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ContextPostRequestDto>> constraintViolations =
                validator.validate(contextPostRequestDto);

        if (!constraintViolations.isEmpty()) {
            String invalidPropertiesMessage = constraintViolations.stream()
                    .map(violation -> "['" + violation.getPropertyPath() + "': " + violation.getMessage() + "]")
                    .collect(Collectors.joining(", "));
            throw new InvalidRequestBodyException("Invalid JSON properties: " + invalidPropertiesMessage);
        }

        UUID collectionId = contextPostRequestDto.getCollectionId();
        UUID resolvedProfileId = QuizzesUtils.resolveProfileId(profileId);
        UUID classId = contextPostRequestDto.getClassId();
        Boolean isCollection = contextPostRequestDto.getIsCollection();
        UUID contextId = contextService.createContext(resolvedProfileId, collectionId, isCollection, classId,
                    contextPostRequestDto.getContextData(), token);
        return new ResponseEntity<>(new IdResponseDto(contextId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Gets Created Context by ID",
            notes = "Gets a Context by its Context ID created by a Profile.\n\n" +
                    "The session token should correspond to the Owner (Profile). " +
                    "The fields `profileId` and `hasStarted` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 403, message = "Anonymous not allowed to run this service"),
            @ApiResponse(code = 404, message = "Content Not Found")
    })
    @RequestMapping(path = "/contexts/{contextId}/created", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getCreatedContext(
            @ApiParam(name = "ContextID", required = true,
                    value = "The ID of the context you want to get from the set of created contexts.")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") String profileId,
            @RequestAttribute(value = "token") String token) {

        QuizzesUtils.rejectAnonymous(profileId);
        ContextEntity context = contextService.findCreatedContext(contextId, UUID.fromString(profileId), token);
        return new ResponseEntity<>(entityMapper.mapContextEntityToContextGetResponseDto(context), HttpStatus.OK);
    }

    @ApiOperation(value = "Gets Assigned Context by ID",
            notes = "Gets the `Active` Context specified by the Context ID assigned to a Profile. " +
                    "The session token should correspond to the Assignee (Profile). " +
                    "Anonymous session token will be rejected by this endpoint.\n\n" +
                    "The fields `isActive` and `modifiedDate` will not be present on the response body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Content Not Found")
    })
    @RequestMapping(path = "/contexts/{contextId}/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getAssignedContext(
            @ApiParam(name = "ContextID", required = true,
                    value = "The ID of the context you want to get from the set of assigned contexts.")
            @PathVariable UUID contextId,
            @RequestAttribute(value = "profileId") String profileId,
            @RequestAttribute(value = "token") String token) throws Exception {

        QuizzesUtils.rejectAnonymous(profileId);
        ContextEntity context = contextService.findAssignedContext(contextId, UUID.fromString(profileId), token);
        return new ResponseEntity<>(entityMapper.mapContextEntityToContextGetResponseDto(context), HttpStatus.OK);
    }

}
