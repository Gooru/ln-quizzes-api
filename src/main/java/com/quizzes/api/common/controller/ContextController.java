package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/quizzes/api")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @ApiOperation(
            value = "Creates an assignment",
            notes = "Creates an assignment of a collection (assessment) to a group of people (students) in " +
                    "a specified context, returning a generated Context ID.")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the Context ID", response = IdResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignContext(@ApiParam(value = "Json body", required = true, name = "Body")
                                           @RequestBody ContextPostRequestDto contextPostRequestDto,
                                           @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                           @RequestHeader(value = "profile-id") UUID profileId) {

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

        IdResponseDto result = contextService.createContext(contextPostRequestDto, Lms.valueOf(lmsId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Get contexts created", notes = "Get all the contexts created by the Owner Profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = CreatedContextGetResponseDto.class)
    })
    @RequestMapping(path = "/v1/contexts/created",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CreatedContextGetResponseDto>> findCreatedContexts(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId,
            @ApiParam(value = "optional query params", required = true, name = "filterMap")
            @RequestParam Map<String, String> filterMap) throws Exception {

        List<CreatedContextGetResponseDto> list = contextService.findCreatedContexts(profileId);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Get created context by ID",
            notes = "Gets a Context by the Context ID from the set of created contexts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = CreatedContextGetResponseDto.class)
    })
    @RequestMapping(path = "/v1/context/created/{contextId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedContextGetResponseDto> findCreatedContextByContextId(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        CreatedContextGetResponseDto result = contextService.findCreatedContextByContextId(contextId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned context by ID",
            notes = "Gets a Context by the Context ID from the set of assigned contexts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextAssignedGetResponseDto.class)
    })
    @RequestMapping(path = "/v1/context/assigned/{contextId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextAssignedGetResponseDto> getAssignedContextByContextId(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {
        ContextAssignedGetResponseDto result = contextService.getAssignedContextByContextId(contextId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned contexts",
            notes = "Get all the ‘active’ contexts assigned to the assignee profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextAssignedGetResponseDto.class)
    })
    @RequestMapping(path = "/v1/contexts/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextAssignedGetResponseDto>> getAssignedContexts(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {
        List<ContextAssignedGetResponseDto> contexts = contextService.getAssignedContexts(profileId);
        return new ResponseEntity<>(contexts, HttpStatus.OK);
    }

    @ApiOperation(value = "Update context", notes = "Update the context metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the Context ID", response = IdResponseDto.class),
    })
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDto> updateContext(
            @PathVariable UUID contextId,
            @ApiParam(value = "Body", required = true, name = "Body")
            @RequestBody ContextPutRequestDto contextPutRequestDto,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        Context context = contextService.update(contextId, contextPutRequestDto, Lms.valueOf(lmsId));

        if (context == null || context.getId() == null) {
            throw new IllegalArgumentException("Error trying to get the updated context");
        }

        IdResponseDto result = new IdResponseDto();
        result.setId(context.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
