package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.EventDTO;
import com.quizzes.api.common.dto.controller.ProfileIdDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @ApiOperation(
            value = "Map context with quizzes",
            notes = "Maps the LMS content with a Quizzes context, returning the Quizzes contextID. " +
                    "If the context does not exist, it will created.")
    @RequestMapping(path = "/v1/context/assignment", method = RequestMethod.POST)
    public ResponseEntity<?> assignContext(@RequestBody AssignmentDTO body,
                                           @RequestHeader(value = "lms-id", defaultValue = "quizzes") String lmsId) {
        Map<String, Object> result = new HashMap<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AssignmentDTO>> constraintViolations = validator.validate(body);

        if (!constraintViolations.isEmpty()) {
            List<String> constraintErrors = new ArrayList<>();
            for (ConstraintViolation violation : constraintViolations) {
                constraintErrors.add(String.format("Error in %s: %s" , violation.getPropertyPath(), violation.getMessage()));
            }
            result.put("Errors", constraintErrors);
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        Context context = contextService.createContext(body, Lms.valueOf(lmsId));

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

}
