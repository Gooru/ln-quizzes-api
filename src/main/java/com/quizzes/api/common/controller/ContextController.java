package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.CommonContextGetResponseDto;
import com.quizzes.api.common.dto.ContextGetAssignedResponseDto;
import com.quizzes.api.common.dto.ContextGetCreatedResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextIdResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.StartContextEventResponseDocDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.request.OnResourceEventRequestDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.ContextServiceDummy;
import com.quizzes.api.common.service.GroupProfileService;
import com.quizzes.api.common.service.GroupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
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

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupProfileService groupProfileService;

    @Autowired
    private JsonParser jsonParser;

    @ApiOperation(
            value = "Creates an assignment",
            notes = "Creates an assignment of a collection (assessment) to a group of people (students) in " +
                    "a specified context, returning a generated Context ID.")
    @ApiResponses({@ApiResponse(code = 200, message = "Context ID", response = ContextIdResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context",
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

        ContextIdResponseDto result = new ContextIdResponseDto(context.getId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Start collection",
            notes = "Sends event to start the Collection attempt associated to the context. " +
                    "If the Collection attempt was not started previously there is not a start action executed. " +
                    "In any case returns the current attempt status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", response = StartContextEventResponseDocDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/context/{contextId}/event/start",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StartContextEventResponseDto> startContextEvent(@PathVariable UUID contextId,
                                                                          @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                                                          @RequestHeader(value = "profile-id") UUID profileId) {
        StartContextEventResponseDto result = contextService.startContextEvent(contextId, profileId);
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
            @ApiResponse(code = 200, message = "Body", response = ContextGetResponseDto.class),
            @ApiResponse(code = 400, message = "Invalid UUID")
    })
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextGetResponseDto> getContext(
            @PathVariable UUID contextId,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetResponseDto contextGetResponseDto = getContextGetResponseDto(contextId);

        return new ResponseEntity<>(contextGetResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Get contexts created", notes = "Get all the contexts created by the Owner Profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetCreatedResponseDto.class)
    })
    @RequestMapping(path = "/v1/contexts/created",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetCreatedResponseDto>> getContextsCreated(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId,
            @ApiParam(value = "optional query params", required = true, name = "filterMap")
            @RequestParam Map<String, String> filterMap) throws Exception {

        ContextGetCreatedResponseDto contextGetCreatedResponseDto = new ContextGetCreatedResponseDto();
        contextGetCreatedResponseDto.setId(UUID.randomUUID());

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        contextGetCreatedResponseDto.setCollection(collection);

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

        contextGetCreatedResponseDto.setAssignees(profiles);

        ContextGetAssignedResponseDto.ContextDataDto contextDataDTO = new ContextGetAssignedResponseDto.ContextDataDto();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataDTO.setContextMap(contextMap);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "First Partial");
        contextDataDTO.setMetadata(metadata);

        contextGetCreatedResponseDto.setContextData(contextDataDTO);

        List<ContextGetCreatedResponseDto> list = new ArrayList<>();
        list.add(contextGetCreatedResponseDto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Get assigned contexts",
            notes = "Get all the ‘active’ contexts assigned to the assignee profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Body", responseContainer = "List",
                    response = ContextGetAssignedResponseDto.class)
    })
    @RequestMapping(path = "/v1/contexts/assigned",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextGetAssignedResponseDto>> getAssignedContexts(
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        ContextGetAssignedResponseDto contextGetAssignedResponseDto = new ContextGetAssignedResponseDto();
        contextGetAssignedResponseDto.setId(UUID.randomUUID());

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        contextGetAssignedResponseDto.setCollection(collection);

        ProfileDTO owner = new ProfileDTO();
        owner.setId(UUID.randomUUID().toString());
        owner.setFirstName("Michael");
        owner.setLastName("Guth");
        owner.setUsername("migut");
        contextGetAssignedResponseDto.setOwner(owner);

        ContextGetAssignedResponseDto.ContextDataDto contextDataDTO = new ContextGetAssignedResponseDto.ContextDataDto();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataDTO.setContextMap(contextMap);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "Second Partial");
        contextDataDTO.setMetadata(metadata);

        contextGetAssignedResponseDto.setContextData(contextDataDTO);

        List<ContextGetAssignedResponseDto> list = new ArrayList<>();
        list.add(contextGetAssignedResponseDto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Update context", notes = "Update the context metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ContextIdResponseDto", response = ContextIdResponseDto.class),
    })
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextIdResponseDto> updateContext(
            @PathVariable UUID contextId,
            @ApiParam(value = "Body", required = true, name = "Body")
            @RequestBody ContextPutRequestDto contextPutRequestDto,
            @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
            @RequestHeader(value = "profile-id") UUID profileId) throws Exception {

        Context context = contextService.update(contextId, contextPutRequestDto, Lms.valueOf(lmsId));

        if (context == null || context.getId() == null) {
            throw new IllegalArgumentException("Error trying to get the updated context");
        }

        return new ResponseEntity<>(new ContextIdResponseDto(context.getId()), HttpStatus.OK);
    }

    private ContextGetResponseDto getContextGetResponseDto(UUID contextId) {

        Context context = contextService.getContext(contextId);

        CollectionDTO collectionDTO = new CollectionDTO();
        collectionDTO.setId(context.getCollectionId().toString());

        Group group = groupService.findById(context.getGroupId());
        ProfileDTO ownerDTO = new ProfileDTO();
        ownerDTO.setId(group.getOwnerProfileId().toString());

        List<GroupProfile> assignees = groupProfileService.findGroupProfilesByGroupId(context.getGroupId());
        List<ProfileDTO> assigneesDTO = new ArrayList<>();
        for (GroupProfile assignee : assignees) {
            ProfileDTO assigneeDTO = new ProfileDTO();
            assigneeDTO.setId(assignee.getId().toString());
            assigneesDTO.add(assigneeDTO);
        }

        CommonContextGetResponseDto.ContextDataDto contextDataDto = new CommonContextGetResponseDto.ContextDataDto();

        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setId(contextId);
        contextGetResponseDto.setCollection(collectionDTO);
        contextGetResponseDto.setOwner(ownerDTO);
        contextGetResponseDto.setAssignees(assigneesDTO);

        Map<String, Object> contextDataMap = jsonParser.parseMap(context.getContextData());

        Map<String, String> contextMap = (Map<String, String>) contextDataMap.get("contextMap");
        Map<String, String> metadata = (Map<String, String>) contextDataMap.get("metadata");
        contextDataDto.setContextMap(contextMap);
        contextDataDto.setMetadata(metadata);

        contextGetResponseDto.setContextData(contextDataDto);

        return contextGetResponseDto;
    }
}
