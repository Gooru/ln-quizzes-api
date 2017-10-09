package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AttemptGetResponseDto;
import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileAttemptsResponseDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.services.AttemptService;
import com.quizzes.api.core.services.ContextProfileService;
import com.quizzes.api.core.services.ContextService;
import com.quizzes.api.util.QuizzesUtils;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttemptControllerTest {

    @InjectMocks
    private AttemptController controller;

    @Mock
    private AttemptService attemptService;

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ContextService contextService;

    private UUID contextId;
    private UUID collectionId;
    private UUID resourceId;
    private UUID ownerId;
    private UUID profileId;
    private UUID currentResourceId;
    private final String token = "TOKEN";

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentResourceId = UUID.randomUUID();
    }

    @Test
    public void getCurrentAttemptByProfile() throws Exception {
        List<AnswerDto> answers = Arrays.asList(new AnswerDto("A"));
        List<PostResponseResourceDto> events =
                Arrays.asList(createPostResponseResourceDto(0, 0, resourceId, answers, 1234, false),
                        createPostResponseResourceDto(0, 0, resourceId, null, 1234, true));

        //Setting ProfileEvents
        ProfileAttemptsResponseDto profileAttemptsResponseDto = new ProfileAttemptsResponseDto();
        profileAttemptsResponseDto.setCurrentResourceId(currentResourceId);
        profileAttemptsResponseDto.setProfileId(profileId);
        profileAttemptsResponseDto.setEvents(events);

        List<ProfileAttemptsResponseDto> profileEvents = Arrays.asList(profileAttemptsResponseDto);

        //Creating studentEventDto mock
        ContextAttemptsResponseDto contextEvents = new ContextAttemptsResponseDto();
        contextEvents.setContextId(contextId);
        contextEvents.setCollectionId(collectionId);
        contextEvents.setProfileAttempts(profileEvents);

        when(attemptService.getCurrentAttemptByProfile(any(UUID.class), any(UUID.class), eq(token))).thenReturn
            (contextEvents);

        ResponseEntity<ContextAttemptsResponseDto> result = controller.getCurrentAttemptByProfile(contextId,
                ownerId.toString(), token);

        verify(attemptService, times(1)).getCurrentAttemptByProfile(contextId, ownerId, token);

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());

        ContextAttemptsResponseDto resultBody = result.getBody();
        assertEquals("Invalid context ID", contextId, resultBody.getContextId());
        assertEquals("Invalid collection ID", collectionId, resultBody.getCollectionId());
        assertEquals("Wrong size in profile events", 1, resultBody.getProfileAttempts().size());

        ProfileAttemptsResponseDto profileEventResult = resultBody.getProfileAttempts().get(0);
        assertEquals("Invalid current resource ID", currentResourceId, profileEventResult.getCurrentResourceId());
        assertEquals("Invalid profile ID", profileId, profileEventResult.getProfileId());
        assertEquals("Invalid number of events", 2, profileEventResult.getEvents().size());

        PostResponseResourceDto eventSkipFalseResult = profileEventResult.getEvents().get(0);
        assertEquals("Score is not 0", 0, eventSkipFalseResult.getScore());
        assertEquals("Reaction is not 0", 0, eventSkipFalseResult.getReaction());
        assertEquals("TimeSpent is not 1234", 1234, eventSkipFalseResult.getTimeSpent());
        assertEquals("Invalid number of answers", 1, eventSkipFalseResult.getAnswer().size());
        assertEquals("Answer is not A", "A", eventSkipFalseResult.getAnswer().get(0).getValue());
        assertEquals("It's not skipped", false, eventSkipFalseResult.getIsSkipped());

        PostResponseResourceDto eventSkipTrueResult = profileEventResult.getEvents().get(1);
        assertEquals("Score is not 0", 0, eventSkipTrueResult.getScore());
        assertEquals("Reaction is not 0", 0, eventSkipTrueResult.getReaction());
        assertEquals("TimeSpent is not 1234", 1234, eventSkipTrueResult.getTimeSpent());
        assertNull("Answer is not null", eventSkipTrueResult.getAnswer());
        assertEquals("It's not skipped", true, eventSkipTrueResult.getIsSkipped());
    }

    @Test
    public void getAttemptIds() throws Exception {
        when(contextService.findCreatedContext(eq(contextId), eq(ownerId), eq(token))).thenReturn(null);

        AttemptIdsResponseDto attemptIdsResponseDto = new AttemptIdsResponseDto();
        when(contextProfileService.findContextProfileIdsByContextIdAndProfileId(eq(contextId), any(UUID.class))).
                thenReturn(new ArrayList<UUID>());

        ResponseEntity<AttemptIdsResponseDto> response = controller.
                getAttemptIds(contextId, UUID.randomUUID().toString(), ownerId.toString(), token);

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class), eq(token));

        verify(contextProfileService, times(1)).findContextProfileIdsByContextIdAndProfileId(any(UUID.class),
                any(UUID.class));
    }

    @Test(expected = InvalidOwnerException.class)
    public void getContextProfileAttemptIdsWithDifferentOwner() throws Exception {

        when(contextService.findCreatedContext(eq(contextId), not(eq(ownerId)), eq(token)))
                .thenThrow(new InvalidOwnerException("Invalid owner"));

        AttemptIdsResponseDto attemptIdsResponseDto = new AttemptIdsResponseDto();
        when(contextProfileService.findContextProfileIdsByContextIdAndProfileId(any(UUID.class), any(UUID.class))).
                thenReturn(new ArrayList<UUID>());

        ResponseEntity<AttemptIdsResponseDto> response = controller.
                getAttemptIds(contextId, UUID.randomUUID().toString(), UUID.randomUUID().toString(), token);

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class), eq(token));

        verify(contextProfileService, times(1)).findContextProfileIdsByContextIdAndProfileId(any(UUID.class),
                any(UUID.class));
    }

    @Test(expected = ContentNotFoundException.class)
    public void getContextProfileAttemptIdsWithNoContext() throws Exception {

        when(contextService.findCreatedContext(not(eq(contextId)), eq(ownerId), eq(token)))
                .thenThrow(new ContentNotFoundException("Context not found"));

        AttemptIdsResponseDto attemptIdsResponseDto = new AttemptIdsResponseDto();
        when(contextProfileService.findContextProfileIdsByContextIdAndProfileId(any(UUID.class), any(UUID.class))).
                thenReturn(new ArrayList<UUID>());

        ResponseEntity<AttemptIdsResponseDto> response =
                controller.getAttemptIds(UUID.randomUUID(), UUID.randomUUID().toString(), ownerId.toString(), token);

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class), eq(token));

        verify(contextProfileService, times(1)).findContextProfileIdsByContextIdAndProfileId(any(UUID.class),
                any(UUID.class));
    }

    @Test
    public void getAttempt() throws Exception {
        UUID attemptId = UUID.randomUUID();

        when(attemptService.getAttempt(attemptId, profileId)).thenReturn(new AttemptGetResponseDto());

        ResponseEntity<AttemptGetResponseDto> response = controller.getAttempt(attemptId, profileId.toString());

        verify(attemptService, times(1)).getAttempt(attemptId, profileId);
    }

    @Test
    public void getAttemptWhenAnonymous() throws Exception {
        UUID attemptId = UUID.randomUUID();
        UUID anonymousId = QuizzesUtils.getAnonymousId();

        when(attemptService.getAttempt(attemptId, anonymousId)).thenReturn(new AttemptGetResponseDto());

        ResponseEntity<AttemptGetResponseDto> response = controller.getAttempt(attemptId, "anonymous");

        verify(attemptService, times(1)).getAttempt(attemptId, anonymousId);
    }

    private PostResponseResourceDto createPostResponseResourceDto(int score, int reaction, UUID resourceId,
                                                                  List<AnswerDto> answers, long timespent,
                                                                  boolean isSkipped) {
        PostResponseResourceDto postResponse = new PostResponseResourceDto();
        postResponse.setScore(score);
        postResponse.setReaction(reaction);
        postResponse.setResourceId(resourceId);
        postResponse.setTimeSpent(timespent);
        postResponse.setIsSkipped(isSkipped);
        postResponse.setAnswer(answers);
        return postResponse;
    }

}
