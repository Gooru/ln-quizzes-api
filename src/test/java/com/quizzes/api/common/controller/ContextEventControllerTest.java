package com.quizzes.api.common.controller;

import com.google.gson.JsonArray;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.ContextEventsResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.AnswerDto;
import com.quizzes.api.common.service.ContextEventService;
import com.quizzes.api.common.service.ContextProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.AssertJUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextEventControllerTest {

    @InjectMocks
    private ContextEventController controller = new ContextEventController();

    @Mock
    private ContextEventService contextEventService;

    @Mock
    private ContextProfileService contextProfileService;

    @Test
    public void startContextEvent() throws Exception {
        UUID id = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        CollectionDto collection = new CollectionDto();
        collection.setId(String.valueOf(collectionId));

        //Setting Answers
        AnswerDto answerDto = new AnswerDto();
        answerDto.setValue("A");
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerDto);

        //Setting Events
        UUID resource1 = UUID.randomUUID();
        PostResponseResourceDto event1 = new PostResponseResourceDto();
        event1.setScore(0);
        event1.setReaction(0);
        event1.setResourceId(resource1);
        event1.setAnswer(answers);
        event1.setTimeSpent(1234);
        event1.setIsSkipped(false);

        UUID resource2 = UUID.randomUUID();
        PostResponseResourceDto event2 = new PostResponseResourceDto();
        event2.setScore(0);
        event2.setReaction(0);
        event2.setResourceId(resource2);
        event2.setAnswer(new ArrayList<>());
        event2.setTimeSpent(1234);
        event2.setIsSkipped(true);

        List<PostResponseResourceDto> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        StartContextEventResponseDto startContext = new StartContextEventResponseDto();
        startContext.setId(id);
        startContext.setCurrentResourceId(resourceId);
        startContext.setCollection(collection);
        startContext.setEvents(events);

        when(contextEventService.startContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result = controller.startContextEvent(UUID.randomUUID(), "quizzes", UUID.randomUUID());

        verify(contextEventService, times(1)).startContextEvent(any(UUID.class), any(UUID.class));

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", id, resultBody.getId());
        assertEquals("Wrong collection id", collection.getId(), resultBody.getCollection().getId());
        assertEquals("Wrong collection id", 2, resultBody.getEvents().size());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        PostResponseResourceDto result1 = resultBody.getEvents().get(0);
        AssertJUnit.assertEquals("Wrong result1 resource1 ID", resource1, result1.getResourceId());
        AssertJUnit.assertEquals("Wrong score for result1", 0, result1.getScore());
        AssertJUnit.assertEquals("Wrong reaction for result1", 0, result1.getReaction());
        AssertJUnit.assertEquals("Wrong timeSpent for result1", 1234, result1.getTimeSpent());
        AssertJUnit.assertEquals("Wrong timeSpent for result1", "A", result1.getAnswer().get(0).getValue());
        assertFalse("IsSkipped is true in result1", result1.getIsSkipped());

        PostResponseResourceDto result2 = resultBody.getEvents().get(1);
        AssertJUnit.assertEquals("Wrong result1 resource2 ID", resource2, result2.getResourceId());
        AssertJUnit.assertEquals("Wrong score for result2", 0, result2.getScore());
        AssertJUnit.assertEquals("Wrong reaction for result2", 0, result2.getReaction());
        AssertJUnit.assertEquals("Wrong timeSpent for result2", 1234, result2.getTimeSpent());
        assertTrue("Answer list is not empty for result2", result2.getAnswer().isEmpty());
        assertTrue("IsSkipped is true in result2", result2.getIsSkipped());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(UUID.randomUUID(), "its_learning", UUID.randomUUID());
        verify(contextEventService, times(1)).finishContextEvent(any(UUID.class), any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    @Test
    public void addEvent() throws Exception {
        ResponseEntity<?> result = controller.onResourceEvent(UUID.randomUUID(), UUID.randomUUID(), new OnResourceEventPostRequestDto(), "quizzes", UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    @Test
    public void getContextEventsIsSkippedFalse() throws Exception {
        UUID contextId = UUID.randomUUID();

        //Setting collectionDto
        UUID collectionId = UUID.randomUUID();
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());

        //Setting Answers
        AnswerDto answerDto = new AnswerDto();
        answerDto.setValue("A");
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(answerDto);

        //Setting Events
        UUID resourceId = UUID.randomUUID();
        PostResponseResourceDto event = new PostResponseResourceDto();
        event.setScore(0);
        event.setReaction(0);
        event.setResourceId(resourceId);
        event.setAnswer(answers);
        event.setTimeSpent(1234);
        event.setIsSkipped(false);
        List<PostResponseResourceDto> events = new ArrayList<>();
        events.add(event);

        //Setting ProfileEvents
        UUID currentResourceId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        ProfileEventResponseDto profileEventResponseDto = new ProfileEventResponseDto();
        profileEventResponseDto.setCurrentResourceId(currentResourceId);
        profileEventResponseDto.setProfileId(profileId);
        profileEventResponseDto.setEvents(events);

        List<ProfileEventResponseDto> profileEvents = new ArrayList<>();
        profileEvents.add(profileEventResponseDto);

        //Creating studentEventDto mock
        ContextEventsResponseDto contextEvents =  new ContextEventsResponseDto();
        contextEvents.setContextId(contextId);
        contextEvents.setCollection(collectionDto);
        contextEvents.setProfileEvents(profileEvents);

        when(contextEventService.getContextEvents(any(UUID.class))).thenReturn(contextEvents);

        ResponseEntity<ContextEventsResponseDto> result = controller.getContextEvents(contextId, "quizzes", UUID.randomUUID());

        verify(contextEventService, times(1)).getContextEvents(contextId);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        ContextEventsResponseDto resultBody = result.getBody();
        assertEquals("Invalid context ID", contextId, resultBody.getContextId());
        assertEquals("Invalid collection ID", collectionId.toString(), resultBody.getCollection().getId());
        assertEquals("Wrong size in profile events", 1, resultBody.getProfileEvents().size());

        ProfileEventResponseDto profileEventResult = resultBody.getProfileEvents().get(0);
        assertEquals("Invalid current resource ID", currentResourceId, profileEventResult.getCurrentResourceId());
        assertEquals("Invalid profile ID", profileId, profileEventResult.getProfileId());
        assertEquals("Invalid number of events", 1, profileEventResult.getEvents().size());

        PostResponseResourceDto eventResult = profileEventResult.getEvents().get(0);
        assertEquals("Score is not 0", 0, eventResult.getScore());
        assertEquals("Reaction is not 0", 0, eventResult.getReaction());
        assertEquals("TimeSpent is not 1234", 1234, eventResult.getTimeSpent());
        assertEquals("Invalid number of answers", 1, eventResult.getAnswer().size());
        assertEquals("Answer is not A", "A", eventResult.getAnswer().get(0).getValue());
        assertEquals("It's not skipped", false, eventResult.getIsSkipped());
    }

    @Test
    public void getContextEventsIsSkippedTrue() throws Exception {
        UUID contextId = UUID.randomUUID();

        //Setting collectionDto
        UUID collectionId = UUID.randomUUID();
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());

        //Setting Answers
        AnswerDto answerDto = new AnswerDto();
        List<AnswerDto> answers = new ArrayList<>();

        //Setting Events
        UUID resourceId = UUID.randomUUID();
        PostResponseResourceDto event = new PostResponseResourceDto();
        event.setScore(0);
        event.setReaction(0);
        event.setResourceId(resourceId);
        event.setAnswer(answers);
        event.setTimeSpent(1234);
        event.setIsSkipped(true);
        List<PostResponseResourceDto> events = new ArrayList<>();
        events.add(event);

        //Setting ProfileEvents
        UUID currentResourceId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        ProfileEventResponseDto profileEventResponseDto = new ProfileEventResponseDto();
        profileEventResponseDto.setCurrentResourceId(currentResourceId);
        profileEventResponseDto.setProfileId(profileId);
        profileEventResponseDto.setEvents(events);

        List<ProfileEventResponseDto> profileEvents = new ArrayList<>();
        profileEvents.add(profileEventResponseDto);

        //Creating studentEventDto mock
        ContextEventsResponseDto contextEvents =  new ContextEventsResponseDto();
        contextEvents.setContextId(contextId);
        contextEvents.setCollection(collectionDto);
        contextEvents.setProfileEvents(profileEvents);

        when(contextEventService.getContextEvents(any(UUID.class))).thenReturn(contextEvents);

        ResponseEntity<ContextEventsResponseDto> result = controller.getContextEvents(contextId, "quizzes", UUID.randomUUID());

        verify(contextEventService, times(1)).getContextEvents(contextId);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        ContextEventsResponseDto resultBody = result.getBody();
        assertEquals("Invalid context ID", contextId, resultBody.getContextId());
        assertEquals("Invalid collection ID", collectionId.toString(), resultBody.getCollection().getId());
        assertEquals("Wrong size in profile events", 1, resultBody.getProfileEvents().size());

        ProfileEventResponseDto profileEventResult = resultBody.getProfileEvents().get(0);
        assertEquals("Invalid current resource ID", currentResourceId, profileEventResult.getCurrentResourceId());
        assertEquals("Invalid profile ID", profileId, profileEventResult.getProfileId());
        assertEquals("Invalid number of events", 1, profileEventResult.getEvents().size());

        PostResponseResourceDto eventResult = profileEventResult.getEvents().get(0);
        assertEquals("Score is not 0", 0, eventResult.getScore());
        assertEquals("Reaction is not 0", 0, eventResult.getReaction());
        assertEquals("TimeSpent is not 1234", 1234, eventResult.getTimeSpent());
        assertTrue("Answer is not empty", eventResult.getAnswer().isEmpty());
        assertEquals("It's not skipped", true, eventResult.getIsSkipped());
    }

}