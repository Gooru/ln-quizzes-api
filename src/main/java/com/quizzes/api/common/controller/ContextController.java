package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.ContextGetAssignedDTO;
import com.quizzes.api.common.dto.ContextGetCreatedDTO;
import com.quizzes.api.common.dto.ContextGetDTO;
import com.quizzes.api.common.dto.ContextPutRequestDTO;
import com.quizzes.api.common.dto.controller.request.OnResourceEventRequestDTO;
import com.quizzes.api.common.dto.controller.response.AnswerDTO;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.response.AssignContextResponseDTO;
import com.quizzes.api.common.dto.controller.response.AttemptDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.ContextServiceDummy;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class ContextController {

    @Autowired
    private ContextService contextService;


    @Autowired
    private ContextServiceDummy contextServiceDummy;

    @ApiOperation(
            value = "Creates an assignment",
            notes = "Creates an assignment of a collection (assessment) to a group of people (students) in " +
                    "a specified context, returning a generated Context ID.")
    @ApiResponses({@ApiResponse(code = 200, message = "Context ID", response = AssignContextResponseDTO.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context/assignment",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignContext(@ApiParam(value = "Json body", required = true, name = "Body")
                                               @RequestBody AssignmentDTO assignmentDTO,
                                           @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                           @RequestHeader(value = "profile-id") UUID profileId) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AssignmentDTO>> constraintViolations = validator.validate(assignmentDTO);

        if (!constraintViolations.isEmpty()) {
            List<String> constraintErrors = new ArrayList<>();
            for (ConstraintViolation violation : constraintViolations) {
                constraintErrors.add(String.format("Error in %s: %s", violation.getPropertyPath(), violation.getMessage()));
            }
            //TODO: the validations are on hold, we're using mocks in the meantime
//            result.put("Errors", constraintErrors);
//            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        //TODO: this is a temporary solution to get mocked or dummy data for "Quizzes"
        Context context = null;
        if (Lms.quizzes.equals(Lms.valueOf(lmsId))) {
            context = contextServiceDummy.createContext(assignmentDTO, Lms.valueOf(lmsId));
        } else {
            context = contextService.createContext(assignmentDTO, Lms.valueOf(lmsId));
        }

        AssignContextResponseDTO result = new AssignContextResponseDTO(context.getId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Start collection",
            notes = "Sends event to start the Collection attempt associated to the context. " +
                    "If the Collection attempt was not started previously there is not a start action executed. " +
                    "In any case returns the current attempt status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = StartContextEventResponseDTO.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context/{contextId}/event/start",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startContextEvent(@PathVariable UUID contextId,
                                               @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                               @RequestHeader(value = "profile-id") UUID profileId) {

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());

        AnswerDTO answer1 = new AnswerDTO("1");
        AnswerDTO answer2 = new AnswerDTO("1,3");
        List<AnswerDTO> answerList = new ArrayList<>();
        answerList.add(answer1);
        answerList.add(answer2);

        AttemptDTO attempt = new AttemptDTO(UUID.randomUUID(), 1500, 1, 8, answerList);
        List<AttemptDTO> attempts = new ArrayList<>();
        attempts.add(attempt);

        StartContextEventResponseDTO result =
                new StartContextEventResponseDTO(contextId, collection, UUID.randomUUID(), attempts);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(value = "Register resource",
            notes = "Sends event to indicate current resource position and provides the data generated" +
                    " in the previous resource (this value could be null in case there is not previous resource)")
    @ApiResponses({@ApiResponse(code = 200, message = "OK")})
    @RequestMapping(path = "/v1/context/{contextId}/event/on-resource/{resourceId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> onResourceEvent(@PathVariable String resourceId,
                                                @PathVariable String contextId,
                                                @ApiParam(value = "Json body", required = true, name = "Body")
                                                    @RequestBody OnResourceEventRequestDTO onResourceEventRequestDTO,
                                                @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                                @RequestHeader(value = "profile-id") UUID profileId) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "Finish event",
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get context", notes = "Gets the context information.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = ContextGetDTO.class),
            @ApiResponse(code = 400, message = "Invalid UUID")
    })
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetDTO> getContext(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetDTO contextGetDTO = new ContextGetDTO();
        contextGetDTO.setId(UUID.randomUUID());

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        contextGetDTO.setCollection(collection);

        ProfileDTO owner = new ProfileDTO();
        owner.setId(UUID.randomUUID().toString());
        owner.setFirstName("Michael");
        owner.setLastName("Guth");
        owner.setUsername("migut");
        contextGetDTO.setOwner(owner);

        List<ProfileDTO> profiles = new ArrayList<>();

        ProfileDTO profile1 = new ProfileDTO();
        profile1.setId(UUID.randomUUID().toString());
        profile1.setFirstName("Karol");
        profile1.setLastName("Fernandez");
        profile1.setUsername("karol1");

        ProfileDTO profile2 = new ProfileDTO();
        profile2.setId(UUID.randomUUID().toString());
        profile2.setFirstName("Roger");
        profile2.setLastName("Stevens");
        profile2.setUsername("rogersteve");

        profiles.add(profile1);
        profiles.add(profile2);

        contextGetDTO.setAssignees(profiles);

        ContextGetAssignedDTO.ContextDataDTO contextDataDTO = new ContextGetAssignedDTO.ContextDataDTO();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataDTO.setContextMap(contextMap);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "First Partial");
        contextDataDTO.setMetadata(metadata);

        contextGetDTO.setContextData(contextDataDTO);

        return new ResponseEntity<>(contextGetDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Get contexts created", notes = "Get all the contexts created by the Owner Profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetCreatedDTO.class)
    })
    @RequestMapping(path = "/v1/contexts/created",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetCreatedDTO>> getContextsCreated(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetCreatedDTO contextGetCreatedDTO = new ContextGetCreatedDTO();
        contextGetCreatedDTO.setId(UUID.randomUUID());

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        contextGetCreatedDTO.setCollection(collection);

        List<ProfileDTO> profiles = new ArrayList<>();

        ProfileDTO profile1 = new ProfileDTO();
        profile1.setId(UUID.randomUUID().toString());
        profile1.setFirstName("Karol");
        profile1.setLastName("Fernandez");
        profile1.setUsername("karol1");

        ProfileDTO profile2 = new ProfileDTO();
        profile2.setId(UUID.randomUUID().toString());
        profile2.setFirstName("Roger");
        profile2.setLastName("Stevens");
        profile2.setUsername("rogersteve");

        profiles.add(profile1);
        profiles.add(profile2);

        contextGetCreatedDTO.setAssignees(profiles);

        ContextGetAssignedDTO.ContextDataDTO contextDataDTO = new ContextGetAssignedDTO.ContextDataDTO();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataDTO.setContextMap(contextMap);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "First Partial");
        contextDataDTO.setMetadata(metadata);

        contextGetCreatedDTO.setContextData(contextDataDTO);

        List<ContextGetCreatedDTO> list = new ArrayList<>();
        list.add(contextGetCreatedDTO);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned contexts",
            notes = "Get all the ‘active’ contexts assigned to the assignee profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetAssignedDTO.class)
    })
    @RequestMapping(path = "/v1/contexts/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetAssignedDTO>> getAssignedContexts(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetAssignedDTO contextGetAssignedDTO = new ContextGetAssignedDTO();
        contextGetAssignedDTO.setId(UUID.randomUUID());

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        contextGetAssignedDTO.setCollection(collection);

        ProfileDTO owner = new ProfileDTO();
        owner.setId(UUID.randomUUID().toString());
        owner.setFirstName("Michael");
        owner.setLastName("Guth");
        owner.setUsername("migut");
        contextGetAssignedDTO.setOwner(owner);

        ContextGetAssignedDTO.ContextDataDTO contextDataDTO = new ContextGetAssignedDTO.ContextDataDTO();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataDTO.setContextMap(contextMap);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "Second Partial");
        contextDataDTO.setMetadata(metadata);

        contextGetAssignedDTO.setContextData(contextDataDTO);

        List<ContextGetAssignedDTO> list = new ArrayList<>();
        list.add(contextGetAssignedDTO);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Update context", notes = "Update the context metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "AssignContextResponseDTO", response = AssignContextResponseDTO.class),
    })
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssignContextResponseDTO> updateContext(
            @PathVariable UUID contextId,
            @ApiParam(value = "Body", required = true, name = "Body")
            @RequestBody ContextPutRequestDTO contextPutRequestDTO,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        Context context = contextService.update(contextId, contextPutRequestDTO);

        if(context == null || context.getId() == null){
            throw new IllegalArgumentException("Error trying to get the updated context");
        }

        return new ResponseEntity<>(new AssignContextResponseDTO(context.getId()), HttpStatus.OK);
    }

}
