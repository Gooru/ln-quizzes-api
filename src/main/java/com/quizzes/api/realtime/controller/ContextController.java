package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.EventDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.ProfileIdDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import io.swagger.annotations.ApiOperation;
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
    @Qualifier("contextServiceImpl")
    private ContextService contextService;

    @Autowired
    @Qualifier("contextServiceDummyImpl")
    private ContextService contextServiceDummy;

    @ApiOperation(
            value = "Creates an assignment",
            notes = "Creates an assignment of a collection (assessment) to a group of people (students) in a specified context, " +
                    "returning a generated Context ID.")
    @RequestMapping(path = "/v1/context/assignment", method = RequestMethod.POST)
    public ResponseEntity<?> assignContext(@RequestBody AssignmentDTO assignmentDTO,
                                           @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId,
                                           @RequestHeader(value = "profile-id") UUID profileId) {
        Map<String, Object> result = new HashMap<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AssignmentDTO>> constraintViolations = validator.validate(assignmentDTO);

        if (!constraintViolations.isEmpty()) {
            List<String> constraintErrors = new ArrayList<>();
            for (ConstraintViolation violation : constraintViolations) {
                constraintErrors.add(String.format("Error in %s: %s", violation.getPropertyPath(), violation.getMessage()));
            }
            result.put("Errors", constraintErrors);
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        //TODO: this is a temporary solution to get mocked or dummy data for "Quizzes"
        Context context = null;
        if (Lms.quizzes.equals(Lms.valueOf(lmsId))) {
            context = contextServiceDummy.createContext(assignmentDTO, Lms.valueOf(lmsId));
        } else {
            context = contextService.createContext(assignmentDTO, Lms.valueOf(lmsId));
        }

        result.put("contextId", context.getId().toString());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(value = "Start collection", notes = "Return the collection if exists, otherwise it will create one")
    @RequestMapping(path = "/v1/event/start/context/{contextId}",
            method = RequestMethod.POST)
    public ResponseEntity<?> startContextEvent(@PathVariable String contextId,
                                               @RequestBody ProfileIdDTO requestBody) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setContextId(contextId);
        eventDTO.setCurrenteResourceId("1");

        ArrayList<Map<String, String>> list = new ArrayList<>();
        Map<String, String> item = new HashMap<>();
        item.put("resourceId", "1");
        item.put("timeSpent", "2452454351");
        item.put("answer", "answer-object");
        item.put("reaction", "2");
        item.put("score", "80");

        list.add(item);
        eventDTO.setCollectionStatus(list);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }


    @ApiOperation(value = "Register resource", notes = "Register resource")
    @RequestMapping(path = "/v1/event/on-resource/{resourceId}/context/{contextId}",
            method = RequestMethod.POST)
    public ResponseEntity<?> onResourceEvent(@PathVariable String resourceId,
                                             @PathVariable String contextId,
                                             @RequestBody ProfileIdDTO requestBody) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Register an event", notes = "Register an event")
    @RequestMapping(path = "/v1/end/context/{contextId}",
            method = RequestMethod.POST)
    public ResponseEntity<?> finishContextEvent(@PathVariable String contextId,
                                                @RequestBody ProfileIdDTO requestBody) throws Exception {
//        contextService.endContext();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @ApiOperation(value = "Get context", notes = "Gets the context information.")
    @ApiResponses({@ApiResponse(code = 200, message = "assignmentDTO", response = AssignmentDTO.class)})
    @RequestMapping(path = "/v1/context/{contextId}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getContext(@PathVariable UUID contextId) throws Exception {

        AssignmentDTO assignmentDTO = new AssignmentDTO();

        CollectionDTO collection = new CollectionDTO();
        collection.setId(UUID.randomUUID().toString());
        assignmentDTO.setCollection(collection);

        ProfileDTO owner = new ProfileDTO();
        owner.setId(UUID.randomUUID().toString());
        owner.setFirstName("Michael");
        owner.setLastName("Guth");
        owner.setUsername("migut");
        assignmentDTO.setOwner(owner);

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

        assignmentDTO.setAssignees(profiles);

        ContextDataDTO contextData = new ContextDataDTO();
        Map<String, String> context = new HashMap<>();
        context.put("classId", UUID.randomUUID().toString());
        contextData.setContextMap(context);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "First Partial");
        contextData.setMetadata(metadata);

        assignmentDTO.setContextData(contextData);

        return new ResponseEntity<>(assignmentDTO, HttpStatus.OK);
    }

}
