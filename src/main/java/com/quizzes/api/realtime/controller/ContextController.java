package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.dto.controller.EventDTO;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.ContextService;
import io.swagger.annotations.ApiOperation;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @ApiOperation(value = "Map context with quizzes", notes = "Maps the LMS content with a Quizzes context, returning the Quizzes contextID. If the context does not exist, it will created.")
    @RequestMapping(path = "/v1/map/context/collection/{externalCollectionId}",
            method = RequestMethod.POST)
    public ResponseEntity<?> mapContext(@PathVariable String externalCollectionId,
                                        @RequestBody ContextDTO body) throws ParseException {
        ResponseEntity<Context> contextResponse = contextService.getContext(externalCollectionId, body);

        Map<String, String> result = new HashMap<String, String>();
        result.put("contextId", contextResponse.getBody().getId().toString());
        return new ResponseEntity<>(result, contextResponse.getStatusCode());
    }


    @ApiOperation(value = "Start collection", notes = "Return the collection if exists, otherwise it will create one")
    @RequestMapping(path = "/v1/event/start/context/{contextId}",
            method = RequestMethod.POST)
    public ResponseEntity<EventDTO> startEvent(@PathVariable String contextId,
                                               @RequestBody String body) throws ParseException {
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
    public ResponseEntity<Object> registerResource(@PathVariable String resourceId,
                                                   @PathVariable String contextId,
                                                   @RequestBody String body) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Register an event", notes = "Register an event")
    @RequestMapping(path = "/v1/end/context/{contextId}",
            method = RequestMethod.POST)
    public ResponseEntity<Object> registerContextEvent(@PathVariable String contextId,
                                                       @RequestBody String body) throws Exception {
//        contextService.endContext();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
