package com.quizzes.api.realtime.controller;

import com.quizzes.api.realtime.model.EventIndex;
import io.swagger.annotations.ApiOperation;
import com.quizzes.api.realtime.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
@RequestMapping("/nucleus/realtime")
public class EventController extends AbstractRealTimeController {

    @Autowired
    private EventService eventService;

    @ApiOperation(value = "Register an event", notes = "Register an event")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/user/{userId}/event",
                    method=RequestMethod.POST)
    public void registerEvent(@PathVariable String classId,
                              @PathVariable String collectionId,
                              @PathVariable String userId,
                              @RequestBody String body) throws Exception {
        String collectionUniqueId = buildCollectionUniqueId(classId, collectionId);
        eventService.saveEvent(collectionUniqueId, userId, body);
    }

    @ApiOperation(value = "Load events by class and collection", notes = "Load events by class and collection")
    @RequestMapping(path="/class/{classId}/collection/{collectionId}/events",
                    method=RequestMethod.GET,
                    produces={"application/json;charset=utf8"})
    public String loadEventsByClassAndCollection(@PathVariable String classId,
                                                 @PathVariable String collectionId) {
        String collectionUniqueId = buildCollectionUniqueId(classId, collectionId);
        Iterable<EventIndex> eventIndexes = eventService.findEventIndexesOrderByUser(collectionUniqueId);
        StringBuilder result = new StringBuilder();
        result.append("{\"content\":[");
        long eventIndexResultSize = result.length();
        eventIndexes.forEach(eventIndex -> {
            result.append("{");
            result.append("\"userUId\":\"").append(eventIndex.getUserId()).append("\",");
            result.append("\"isCompleteAttempt\":").append(eventIndex.isComplete()).append(",");
            result.append("\"usageData\":[");
            long eventResultSize = result.length();
//            Iterable<Event> events = eventService.findEvents(eventIndex.getEvents());
            eventIndex.getEvents().forEach((event) -> {
                result.append(event.getEventBody());
                result.append(",");
            });
            if (result.length() > eventResultSize) {
                result.deleteCharAt(result.length() - 1);   // Removes the last comma (,)
            }
            result.append("]},");
        });
        if (result.length() > eventIndexResultSize) {
            result.deleteCharAt(result.length() - 1);   // Removes the last comma (,)
        }
        result.append("]}");
        return result.toString();
    }

}
