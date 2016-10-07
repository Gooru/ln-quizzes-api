package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.ContextService;
import io.swagger.annotations.ApiOperation;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class ContextController {

    @Autowired
    private JsonParser jsonParser;

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
    @RequestMapping(path = "/event/start/context/{contextId}",
            method = RequestMethod.POST)
    public void startEvent(@PathVariable UUID context,
                           @RequestBody String body) throws ParseException {
//        ResponseEntity<Context> contextResponse = contextService.startEvent(context);
//        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @ApiOperation(value = "Register resource", notes = "Register resource")
    @RequestMapping(path = "/on-resource/{resourceId}/context/{contextId}",
            method = RequestMethod.POST)
    public void registerResource(@PathVariable String resourceId,
                                 @PathVariable String contextId,
                                 @RequestBody String body) throws Exception {
//        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Register an event", notes = "Register an event")
    @RequestMapping(path = "/end/context/{contextId}",
            method = RequestMethod.POST)
    public void registerContextEvent(@PathVariable String contextId,
                                     @RequestBody String body) throws Exception {
//        contextService.endContext();
//        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
