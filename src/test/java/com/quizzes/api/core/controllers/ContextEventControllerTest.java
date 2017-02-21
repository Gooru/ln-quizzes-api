package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileAttemptsResponseDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.services.ContextEventService;
import com.quizzes.api.core.services.ContextProfileService;
import com.quizzes.api.core.services.ContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextEventControllerTest {

    @InjectMocks
    private ContextEventController controller;

    @Mock
    private ContextEventService contextEventService;

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ContextService contextService;

    private UUID contextId;
    private UUID collectionId;
    private UUID resourceId;
    private UUID profileId;
    private UUID anonymousId;
    private String token;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        anonymousId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        token = UUID.randomUUID().toString();
    }

    @Test
    public void startContextEvent() throws Exception {
        List<AnswerDto> answers = Arrays.asList(new AnswerDto("A"));

        //Setting Events
        UUID resource1 = UUID.randomUUID();
        PostResponseResourceDto event1 = createPostResponseResourceDto(0, 0, resource1, answers, 1234, false);

        UUID resource2 = UUID.randomUUID();
        PostResponseResourceDto event2 = createPostResponseResourceDto(0, 0, resource2, new ArrayList<>(), 1234, true);

        List<PostResponseResourceDto> events = Arrays.asList(event1, event2);

        StartContextEventResponseDto startContext = new StartContextEventResponseDto();
        startContext.setContextId(contextId);
        startContext.setCurrentResourceId(resourceId);
        startContext.setCollectionId(collectionId);
        startContext.setEvents(events);


        when(contextEventService.processStartContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result = controller.startContextEvent(contextId,
                profileId.toString());

        verify(contextEventService, times(1)).processStartContextEvent(contextId, profileId);

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", contextId, resultBody.getContextId());
        assertEquals("Wrong collection id", collectionId, resultBody.getCollectionId());
        assertEquals("Wrong collection id", 2, resultBody.getEvents().size());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        PostResponseResourceDto result1 = resultBody.getEvents().get(0);
        assertEquals("Wrong result1 resource1 ID", resource1, result1.getResourceId());
        assertEquals("Wrong score for result1", 0, result1.getScore());
        assertEquals("Wrong reaction for result1", 0, result1.getReaction());
        assertEquals("Wrong timeSpent for result1", 1234, result1.getTimeSpent());
        assertEquals("Wrong timeSpent for result1", "A", result1.getAnswer().get(0).getValue());
        assertFalse("IsSkipped is true in result1", result1.getIsSkipped());

        PostResponseResourceDto result2 = resultBody.getEvents().get(1);
        assertEquals("Wrong result1 resource2 ID", resource2, result2.getResourceId());
        assertEquals("Wrong score for result2", 0, result2.getScore());
        assertEquals("Wrong reaction for result2", 0, result2.getReaction());
        assertEquals("Wrong timeSpent for result2", 1234, result2.getTimeSpent());
        assertTrue("Answer list is not empty for result2", result2.getAnswer().isEmpty());
        assertTrue("IsSkipped is true in result2", result2.getIsSkipped());
    }

    @Test
    public void startContextEventForAnonymous() throws Exception {
        List<AnswerDto> answers = Arrays.asList(new AnswerDto("A"));

        UUID resource1 = UUID.randomUUID();
        PostResponseResourceDto event1 = createPostResponseResourceDto(0, 0, resource1, answers, 1234, false);

        UUID resource2 = UUID.randomUUID();
        PostResponseResourceDto event2 = createPostResponseResourceDto(0, 0, resource2, new ArrayList<>(), 1234, true);

        List<PostResponseResourceDto> events = Arrays.asList(event1, event2);

        StartContextEventResponseDto startContext = new StartContextEventResponseDto();
        startContext.setContextId(contextId);
        startContext.setCurrentResourceId(resourceId);
        startContext.setCollectionId(collectionId);
        startContext.setEvents(events);

        when(contextEventService.processStartContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result =
                controller.startContextEvent(contextId, anonymousId.toString());

        verify(contextEventService, times(1)).processStartContextEvent(contextId, anonymousId);

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", contextId, resultBody.getContextId());
        assertEquals("Wrong collection id", collectionId, resultBody.getCollectionId());
        assertEquals("Wrong collection id", 2, resultBody.getEvents().size());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        PostResponseResourceDto result1 = resultBody.getEvents().get(0);
        assertEquals("Wrong result1 resource1 ID", resource1, result1.getResourceId());
        assertEquals("Wrong score for result1", 0, result1.getScore());
        assertEquals("Wrong reaction for result1", 0, result1.getReaction());
        assertEquals("Wrong timeSpent for result1", 1234, result1.getTimeSpent());
        assertEquals("Wrong timeSpent for result1", "A", result1.getAnswer().get(0).getValue());
        assertFalse("IsSkipped is true in result1", result1.getIsSkipped());

        PostResponseResourceDto result2 = resultBody.getEvents().get(1);
        assertEquals("Wrong result1 resource2 ID", resource2, result2.getResourceId());
        assertEquals("Wrong score for result2", 0, result2.getScore());
        assertEquals("Wrong reaction for result2", 0, result2.getReaction());
        assertEquals("Wrong timeSpent for result2", 1234, result2.getTimeSpent());
        assertTrue("Answer list is not empty for result2", result2.getAnswer().isEmpty());
        assertTrue("IsSkipped is true in result2", result2.getIsSkipped());
    }

    @Test
    public void processFinishContextEvent() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(contextId, profileId.toString());
        verify(contextEventService, times(1)).processFinishContextEvent(contextId, profileId);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    @Test
    public void processFinishContextEventForAnonymous() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(contextId, "anonymous");
        verify(contextEventService, times(1)).processFinishContextEvent(contextId, anonymousId);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    @Test
    public void addEvent() throws Exception {
        OnResourceEventPostRequestDto body = new OnResourceEventPostRequestDto();
        ResponseEntity<?> result = controller.onResourceEvent(resourceId, contextId,
                body, profileId);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    private PostResponseResourceDto createPostResponseResourceDto(int score, int reaction, UUID resourceId,
                                                                  List<AnswerDto> answers, long timespent,
                                                                  boolean isSkipped) {
        PostResponseResourceDto event = new PostResponseResourceDto();
        event.setScore(score);
        event.setReaction(reaction);
        event.setResourceId(resourceId);
        event.setAnswer(answers);
        event.setTimeSpent(timespent);
        event.setIsSkipped(isSkipped);
        return event;
    }

}