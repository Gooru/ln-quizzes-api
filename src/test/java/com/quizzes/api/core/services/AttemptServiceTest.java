package com.quizzes.api.core.services;


import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AttemptGetResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;

import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileAttemptsResponseDto;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.boot.json.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AttemptService.class)
public class AttemptServiceTest {

    @InjectMocks
    private AttemptService attemptService = spy(new AttemptService());

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ContextProfileEventService contextProfileEventService;

    @Mock
    private ContextService contextService;

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private AssigneeEventEntity assigneeEventEntity;

    @Mock
    private CurrentContextProfileService currentContextProfileService;

    @Mock
    private ActiveMQClientService activeMQClientService;

    @Mock
    private CollectionService collectionService;

    @Mock
    private Gson gson = new Gson();

    @Mock
    private JsonParser jsonParser;

    private UUID collectionId;
    private UUID contextId;
    private UUID resourceId;
    private UUID contextProfileId;
    private UUID ownerId;
    private UUID profileId;

    @Before
    public void beforeEachTest() {
        collectionId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        profileId = UUID.randomUUID();
    }

    @Test
    public void getCurrentAttemptByProfile() throws Exception {
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        AssigneeEventEntity assigneeEventEntity = createAssignedEventEntity();
        List<AssigneeEventEntity> events = new ArrayList<>();

        contextEventsMap.put(profileId, events);

        ProfileAttemptsResponseDto profileAttempt = createProfileAttemptsResponseDto();
        List<ProfileAttemptsResponseDto> profileAttempts = Arrays.asList(profileAttempt);
        ContextEntity contextEntity = createAssignedContextEntity();

        when(contextService.findCreatedContext(contextId, ownerId)).thenReturn(contextEntity);
        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        doReturn(profileAttempts).when(attemptService, "mapProfileAttempts",
                contextEventsMap);

        ContextAttemptsResponseDto result = attemptService.getCurrentAttemptByProfile(contextId, ownerId);

        verify(contextService, times(1)).findCreatedContext(contextId, ownerId);
        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verifyPrivate(attemptService, times(1)).invoke("mapProfileAttempts", contextEventsMap);

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId, result.getCollectionId());
        assertEquals("Wrong size of events ID", 1, result.getProfileAttempts().size());

        ProfileAttemptsResponseDto profileResult1 = result.getProfileAttempts().get(0);
        assertEquals("Wrong profile ID for assigneeId1", profileId, profileResult1.getProfileId());
        assertEquals("Wrong current content id", resourceId, profileResult1.getCurrentResourceId());
        assertNull("Event list is not null", profileResult1.getEvents());
    }

//    private List<ProfileAttemptsResponseDto> mapProfileAttempts(Map<UUID, List<AssigneeEventEntity>> assigneeEvents) {
//        return assigneeEvents.entrySet().stream().map(entity -> {
//            List<AssigneeEventEntity> assigneeEventEntityList = entity.getValue();
//            ProfileAttemptsResponseDto profileEvent = new ProfileAttemptsResponseDto();
//            profileEvent.setProfileId(entity.getKey());
//
//            AssigneeEventEntity anyAssigneeEventEntity = assigneeEventEntityList.get(0);
//            if (!assigneeEventEntityList.isEmpty()) {
//                profileEvent.setCurrentResourceId(anyAssigneeEventEntity.getCurrentResourceId());
//                profileEvent.setIsComplete(anyAssigneeEventEntity.getIsComplete());
//            }
//
//            profileEvent.setEvents(assigneeEventEntityList.stream()
//                    .filter(studentEventEntity -> studentEventEntity.getEventData() != null)
//                    .map(studentEventEntity -> gson.fromJson(studentEventEntity.getEventData(),
//                            PostResponseResourceDto.class)).collect(Collectors.toList()));
//
//            EventSummaryDataDto eventSummaryDataDto =
//                    gson.fromJson(anyAssigneeEventEntity.getEventsSummary(), EventSummaryDataDto.class);
//            profileEvent.setEventSummary(eventSummaryDataDto);
//
//            return profileEvent;
//
//        }).collect(Collectors.toList());
//    }

    @Test
    public void mapProfileAttempts() throws Exception {
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        AssigneeEventEntity assigneeEventEntity = createAssignedEventEntity();
        List<AssigneeEventEntity> events = Arrays.asList(assigneeEventEntity);
        contextEventsMap.put(profileId, events);

        List<ProfileAttemptsResponseDto> resultList = WhiteboxImpl.invokeMethod(attemptService,
                "mapProfileAttempts", contextEventsMap);

        ProfileAttemptsResponseDto result = resultList.get(0);
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
        assertEquals("Wrong currentResource ID", resourceId, result.getCurrentResourceId());
        assertFalse("IsComplete is true", result.getIsComplete());
        assertEquals("Wrong events size", 1, result.getEvents().size());

        EventSummaryDataDto eventResult = result.getEventSummary();
        assertEquals("Wrong total time", 0, eventResult.getTotalTimeSpent());
        assertEquals("Wrong average reaction", 0, eventResult.getAverageReaction());
        assertEquals("Wrong average score", 0, eventResult.getAverageScore());
        assertEquals("Wrong total answered", 0, eventResult.getTotalAnswered());
        assertEquals("Wrong total correct", 0, eventResult.getTotalCorrect());

    }

    @Test
    public void getAttempt() throws Exception {

        UUID attemptId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();

        EventSummaryDataDto eventEntity1Summary = new EventSummaryDataDto();
        eventEntity1Summary.setAverageScore((short)50);
        eventEntity1Summary.setTotalCorrect((short)1);
        eventEntity1Summary.setAverageReaction((short)2);
        eventEntity1Summary.setTotalAnswered((short)1);
        eventEntity1Summary.setTotalTimeSpent(2500);
        String eventSummary1 = gson.toJson(eventEntity1Summary);

        PostResponseResourceDto eventEntity1Data = new PostResponseResourceDto();
        eventEntity1Data.setResourceId(UUID.randomUUID());
        eventEntity1Data.setIsSkipped(false);
        eventEntity1Data.setScore(100);
        AnswerDto answerDto1 = new AnswerDto();
        answerDto1.setValue("A");
        List<AnswerDto> answers1 = new ArrayList<>();
        answers1.add(answerDto1);
        eventEntity1Data.setAnswer(answers1);
        eventEntity1Data.setReaction(1);
        eventEntity1Data.setTimeSpent(1000);
        String eventData1 = gson.toJson(eventEntity1Data);
        ContextProfileEventEntity eventEntity1 = createContextProfileEventEntity(attemptId, contextId, profileId,
                collectionId, eventData1, eventSummary1);

        PostResponseResourceDto eventEntity2Data = new PostResponseResourceDto();
        eventEntity2Data.setResourceId(UUID.randomUUID());
        eventEntity2Data.setIsSkipped(false);
        eventEntity2Data.setScore(0);
        AnswerDto answerDto2 = new AnswerDto();
        answerDto2.setValue("B");
        List<AnswerDto> answers2 = new ArrayList<>();
        answers2.add(answerDto2);
        eventEntity2Data.setAnswer(answers1);
        eventEntity2Data.setReaction(3);
        eventEntity2Data.setTimeSpent(1500);
        String eventData2 = gson.toJson(eventEntity1Data);
        ContextProfileEventEntity eventEntity2 = createContextProfileEventEntity(attemptId, contextId, profileId,
                collectionId, eventData2, eventSummary1);

        List<ContextProfileEventEntity> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(eventEntity1);
        contextProfileEvents.add(eventEntity2);

        when(contextProfileEventService.findByContextProfileIdAndProfileId(attemptId, profileId)).
                thenReturn(contextProfileEvents);

        AttemptGetResponseDto result = attemptService.getAttempt(attemptId, profileId);

        verify(contextProfileEventService, times(1)).
                findByContextProfileIdAndProfileId(attemptId, profileId);

        assertEquals("Average not equal", eventEntity1Summary.getAverageReaction(),
                result.getEventSummary().getAverageReaction());
        assertEquals("Score not equal", eventEntity1Summary.getAverageScore(),
                result.getEventSummary().getAverageScore());
        assertEquals("Average not equal", eventEntity1Summary.getAverageScore(),
                result.getEventSummary().getAverageScore());
        assertEquals("Number of events is wrong", 2, result.getEvents().size());
        assertTrue("Wrong score", result.getEvents().get(0).getScore() == 0 ||
                result.getEvents().get(0).getScore() == 100);
        assertTrue("Wrong score", result.getEvents().get(1).getScore() == 0 ||
                result.getEvents().get(0).getScore() == 100);
    }

    /**
     * Because we can create a {@link Context} with a Collection with no Resources
     * then the attempts will have no Resources.
     * @throws Exception
     */
    @Test
    public void getAttemptWithoutResource() throws Exception {

        UUID attemptId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();

        EventSummaryDataDto eventEntity1Summary = new EventSummaryDataDto();
        eventEntity1Summary.setAverageScore((short)0);
        eventEntity1Summary.setTotalCorrect((short)0);
        eventEntity1Summary.setAverageReaction((short)0);
        eventEntity1Summary.setTotalAnswered((short)0);
        eventEntity1Summary.setTotalTimeSpent(0);
        String eventSummary1 = gson.toJson(eventEntity1Summary);

        ContextProfileEventEntity eventEntity1 = createContextProfileEventEntity(attemptId, contextId, profileId,
                collectionId, null, eventSummary1);

        List<ContextProfileEventEntity> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(eventEntity1);

        when(contextProfileEventService.findByContextProfileIdAndProfileId(attemptId, profileId)).
                thenReturn(contextProfileEvents);

        AttemptGetResponseDto result = attemptService.getAttempt(attemptId, profileId);

        verify(contextProfileEventService, times(1)).
                findByContextProfileIdAndProfileId(attemptId, profileId);

        assertEquals("Average not equal", eventEntity1Summary.getAverageReaction(),
                result.getEventSummary().getAverageReaction());
        assertEquals("Score not equal", eventEntity1Summary.getAverageScore(),
                result.getEventSummary().getAverageScore());
        assertEquals("Average not equal", eventEntity1Summary.getAverageScore(),
                result.getEventSummary().getAverageScore());
        assertEquals("Number of events is wrong", 0, result.getEvents().size());
    }

    private AssignedContextEntity createAssignedContextEntity() {
        AssignedContextEntity assignedContextEntity = mock(AssignedContextEntity.class);

        when(assignedContextEntity.getContextProfileId()).thenReturn(contextProfileId);
        when(assignedContextEntity.getContextId()).thenReturn(contextId);
        when(assignedContextEntity.getCollectionId()).thenReturn(collectionId);
        when(assignedContextEntity.getProfileId()).thenReturn(profileId);

        return assignedContextEntity;
    }

    private ProfileAttemptsResponseDto createProfileAttemptsResponseDto() {
        ProfileAttemptsResponseDto profileAttempts = new ProfileAttemptsResponseDto();
        profileAttempts.setCurrentResourceId(resourceId);
        profileAttempts.setIsComplete(false);
        profileAttempts.setProfileId(profileId);
        return profileAttempts;
    }

    private AssigneeEventEntity createAssignedEventEntity() {
        UUID bodyResourceId = UUID.randomUUID(); //Last resource id
        long timeSpent = 1000;
        int reaction = 3;
        int score = 100;
        boolean isSkipped = false;
        String answerValue = "D";

        AssigneeEventEntity entity = mock(AssigneeEventEntity.class);
        when(entity.getCurrentResourceId()).thenReturn(resourceId);
        when(entity.getEventsSummary()).thenReturn(gson.toJson(new EventSummaryDataDto()));
        when(entity.getEventData()).thenReturn(gson.toJson(createResponseResourceDto(bodyResourceId,
                score, reaction, timeSpent, createAnswerList(answerValue), isSkipped)));

        return entity;
    }

    private ContextProfileEventEntity createContextProfileEventEntity(UUID attemptIdP, UUID contextIdP, UUID profileIdP,
                                                                      UUID collectionIdP, String dataP, String summaryP) {
        ContextProfileEventEntity result = mock(ContextProfileEventEntity.class);
        when(result.getContextProfileId()).thenReturn(attemptIdP);
        when(result.getContextId()).thenReturn(contextIdP);
        when(result.getProfileId()).thenReturn(profileIdP);
        when(result.getCollectionId()).thenReturn(collectionIdP);
        when(result.getResourceId()).thenReturn(UUID.randomUUID());
        when(result.getCurrentResourceId()).thenReturn(UUID.randomUUID());
        when(result.getEventData()).thenReturn(dataP);
        when(result.getEventsSummary()).thenReturn(summaryP);
        return result;
    }

    private PostResponseResourceDto createResponseResourceDto(UUID resourceId, int score, int reaction, long timeSpent,
                                                              List<AnswerDto> answers, boolean isSkipped) {
        PostResponseResourceDto resourceDto = new PostResponseResourceDto();
        resourceDto.setResourceId(resourceId);
        resourceDto.setScore(score);
        resourceDto.setReaction(reaction);
        resourceDto.setTimeSpent(timeSpent);
        resourceDto.setAnswer(answers);
        resourceDto.setIsSkipped(isSkipped);
        return resourceDto;
    }

    private List<AnswerDto> createAnswerList(String answer) {
        List<AnswerDto> answers = new ArrayList<>();
        AnswerDto answerDto = new AnswerDto(answer);
        answers.add(answerDto);
        return answers;
    }


}