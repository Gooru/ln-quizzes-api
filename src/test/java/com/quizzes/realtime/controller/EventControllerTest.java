package com.quizzes.realtime.controller;

import com.quizzes.realtime.model.Event;
import com.quizzes.realtime.model.EventIndex;
import com.quizzes.realtime.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest{

    @InjectMocks
    private EventController eventController = new EventController();

    @Mock
    private EventService eventService;

    @Test
    public void registerEvent() throws Exception {
        eventController.registerEvent("classId","collectionId","userId","{body}");
        verify(eventService, times(1)).saveEvent(Mockito.eq("classId_collectionId"), Mockito.eq("userId"),Mockito.eq("{body}"));
    }

    @Test
    public void loadEventsByClassAndCollection() throws Exception {
        EventIndex mockIndex = new EventIndex("classId_collectionId", "userId");
        List<Event> events = new ArrayList<>();
        events.add(new Event(mockIndex, "{\"description\": \"description test\"}"));

        mockIndex = new EventIndex("classId_collectionId", "userId", false, events);
        List<EventIndex> eventIndex = new ArrayList<>();
        eventIndex.add(mockIndex);

        when(eventService.findEventIndexesOrderByUser("classId_collectionId")).thenReturn(eventIndex);
        String result = eventController.loadEventsByClassAndCollection("classId","collectionId");

        assertEquals("{\"content\":[{\"userUId\":\"userId\",\"isCompleteAttempt\":false,\"usageData\":[{\"description\": \"description test\"}]}]}",result);
    }

}