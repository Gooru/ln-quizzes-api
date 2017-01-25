package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ContextEventsResponseDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileEventResponseDto;
import com.quizzes.api.core.dtos.QuestionDataDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.Resource;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextEventService.class)
public class ContextEventServiceTest {

    @InjectMocks
    private ContextEventService contextEventService = spy(new ContextEventService());

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ContextProfileEventService contextProfileEventService;

    @Mock
    private ContextService contextService;

    @Mock
    private ProfileService profileService;

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private AssigneeEventEntity assigneeEventEntity;

    @Mock
    private CurrentContextProfileService currentContextProfileService;

    @Mock
    private ActiveMQClientService activeMQClientService;

    @Mock
    private Gson gson = new Gson();

    @Mock
    private JsonParser jsonParser;

    private UUID collectionId;
    private UUID contextId;
    private UUID resourceId;
    private UUID previousResourceId;
    private UUID contextProfileId;
    private UUID startContextId;
    private UUID ownerId;
    private UUID profileId;
    private CurrentContextProfile currentContextProfile;
    private String trueFalseQuestion = "true_false";
    private String singleChoiceQuestion = "single_choice";

    @Before
    public void beforeEachTest() {
        collectionId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        previousResourceId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        startContextId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentContextProfile = new CurrentContextProfile();
    }

    @Test
    public void processStartContextEventWithCurrentContextProfileIsCompleteFalse() throws Exception {
        Context context = createContext();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);

        when(contextService.findByIdAndAssigneeId(contextId, profileId)).thenReturn(context);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);
        doReturn(createStartContextEventResponseDto())
                .when(contextEventService, "resumeStartContextEvent", context, contextProfile);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findByIdAndAssigneeId(contextId, profileId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verifyPrivate(contextEventService, Mockito.times(1)).invoke("resumeStartContextEvent", context, contextProfile);
        assertEquals("Wrong startContext ID", startContextId, result.getId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId.toString(), result.getCollection().getId());
        assertEquals("There are events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContextEventWithCurrentContextProfileIsCompleteTrue() throws Exception {
        Context context = createContext();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(true);

        when(contextService.findById(contextId)).thenReturn(context);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "createStartContextEvent", context, profileId);

        when(contextService.findByIdAndAssigneeId(any(UUID.class), any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenThrow(ContentNotFoundException.class);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "createStartContextEvent", context, profileId);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findByIdAndAssigneeId(contextId, profileId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verifyPrivate(contextEventService, Mockito.times(1)).invoke("createStartContextEvent", context, profileId);
        assertEquals("Wrong startContext ID", startContextId, result.getId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId.toString(), result.getCollection().getId());
        assertEquals("There are events", 0, result.getEvents().size());
    }

    @Test
    public void createStartContextEvent() throws Exception {
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();

        doReturn(contextProfile).when(contextEventService, "createContextProfile", context.getId(), profileId);
        doReturn(createStartContextEventResponseDto())
                .when(contextEventService, "prepareStartContextEventResponse", eq(context), eq(contextProfile), any());
        doNothing().when(contextEventService, "doCreateStartContextEventTransaction", contextProfile);
        doNothing().when(contextEventService, "sendStartEventMessage", contextProfile, true);

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "createStartContextEvent", context, profileId);

        verifyPrivate(contextEventService, Mockito.times(1)).invoke("createContextProfile", context.getId(), profileId);
        verifyPrivate(contextEventService, Mockito.times(1))
                .invoke("doCreateStartContextEventTransaction", contextProfile);
        verifyPrivate(contextEventService, Mockito.times(1)).invoke("sendStartEventMessage", contextProfile, true);
        verifyPrivate(contextEventService, Mockito.times(1))
                .invoke("prepareStartContextEventResponse", eq(context), eq(contextProfile), any());
        assertEquals("Wrong startContext ID", startContextId, result.getId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId.toString(), result.getCollection().getId());
        assertEquals("There are events", 0, result.getEvents().size());
    }

    @Test
    public void createContextProfilePrivateMethod() throws Exception {
        Resource resource = createResource();
        doReturn(resource).when(contextEventService, "findFirstResourceByContextId", contextId);

        ContextProfile result =
                WhiteboxImpl.invokeMethod(contextEventService, "createContextProfile", contextId, profileId);

        verifyPrivate(contextEventService, Mockito.times(1)).invoke("findFirstResourceByContextId", contextId);
        assertEquals("Wrong contextId", contextId, result.getContextId());
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());

        EventSummaryDataDto eventSummaryResult = gson.fromJson(result.getEventSummaryData(), EventSummaryDataDto.class);
        assertEquals("Wrong averageReaction", 0, eventSummaryResult.getAverageReaction());
        assertEquals("Wrong averageScore", 0, eventSummaryResult.getAverageScore());
        assertEquals("Wrong total answered", 0, eventSummaryResult.getTotalAnswered());
        assertEquals("Wrong total correct", 0, eventSummaryResult.getTotalCorrect());
        assertEquals("Wrong total time spent", 0, eventSummaryResult.getTotalTimeSpent());
    }

    @Test
    public void createCurrentContextProfilePrivateMethod() throws Exception {
        ContextProfile contextProfile = createContextProfile();

        CurrentContextProfile result =
                WhiteboxImpl.invokeMethod(contextEventService, "createCurrentContextProfile", contextProfile);

        assertEquals("Wrong contextProfileId", contextProfileId, result.getContextProfileId());
        assertEquals("Wrong contextId", contextId, result.getContextId());
        assertEquals("Wrong profileId", profileId, result.getProfileId());
    }

    @Test
    public void findFirstResourceByContextId() throws Exception {
        when(resourceService.findFirstByContextIdOrderBySequence(contextId)).thenReturn(createResource());

        Resource result =
                WhiteboxImpl.invokeMethod(contextEventService, "findFirstResourceByContextId", contextId);

        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(contextId);
        assertEquals("Wrong resourceId", resourceId, result.getId());
    }

    @Test
    public void doCreateStartContextEventTransaction() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        when(contextProfileService.save(contextProfile)).thenReturn(contextProfile);
        doReturn(currentContextProfile).when(contextEventService, "createCurrentContextProfile", contextProfile);

        WhiteboxImpl.invokeMethod(contextEventService, "doCreateStartContextEventTransaction", contextProfile);

        verifyPrivate(contextEventService, times(1)).invoke("createCurrentContextProfile", contextProfile);
        verify(contextProfileService, times(1)).save(contextProfile);
        verify(currentContextProfileService, times(1)).delete(currentContextProfile);
        verify(currentContextProfileService, times(1)).create(currentContextProfile);
    }

    @Test
    public void sendStartEventMessage() throws Exception {
        ContextProfile contextProfile = createContextProfile();

        WhiteboxImpl.invokeMethod(contextEventService, "sendStartEventMessage", contextProfile, true);

        verify(activeMQClientService, times(1)).sendStartContextEventMessage(eq(contextId), eq(profileId), any());
    }

    @Test
    public void prepareStartContextEventResponse() throws Exception {
        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "prepareStartContextEventResponse",
                        createContext(), createContextProfile(), new ArrayList<>());

        assertEquals("Wrong ID", contextId, result.getId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong events size", 0, result.getEvents().size());
    }

    @Test
    public void resumeStartContextEvent() throws Exception {
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();

        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        doNothing().when(contextEventService, "sendStartEventMessage", contextProfile, false);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                context, contextProfile, contextProfileEvents);

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "resumeStartContextEvent", context, contextProfile);

        verifyPrivate(contextEventService, times(1)).invoke("sendStartEventMessage", contextProfile, false);
        assertEquals("Wrong startContext ID", startContextId, result.getId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId.toString(), result.getCollection().getId());
        assertEquals("There are events", 0, result.getEvents().size());
    }

    @Test
    public void processOnResourceEventWithoutAnswer() throws Exception {
        Resource resource = createResource();
        Resource previousResource = createResource();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));
        QuestionDataDto questionDataDto = createQuestionDataDto(answers, trueFalseQuestion);
        previousResource.setResourceData(gson.toJson(questionDataDto));

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        body.getPreviousResource().getAnswer().remove(0);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        List<Resource> collectionResources =  new ArrayList<>();
        collectionResources.add(resource);
        collectionResources.add(previousResource);

        when(contextService.findByIdAndAssigneeId(any(UUID.class), any(UUID.class))).thenReturn(new Context());
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(resourceService.findByCollectionId(any(UUID.class))).thenReturn(collectionResources);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(contextService, times(1)).findByIdAndAssigneeId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findByCollectionId(any(UUID.class));
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
        verifyPrivate(contextEventService, times(0)).invoke("calculateScoreByQuestionType", any(), any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("createContextProfileEvent", any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Test
    public void processOnResourceEventWithAnswer() throws Exception {
        Resource resource = createResource();

        Resource previousResource = createResource();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));
        QuestionDataDto questionDataDto = createQuestionDataDto(answers, trueFalseQuestion);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        previousResource.setResourceData(gson.toJson(questionDataDto));

        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();

        List<Resource> collectionResources =  new ArrayList<>();
        collectionResources.add(resource);
        collectionResources.add(previousResource);

        when(contextService.findByIdAndAssigneeId(any(UUID.class), any(UUID.class))).thenReturn(new Context());
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(resourceService.findByCollectionId(any(UUID.class))).thenReturn(collectionResources);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(100).when(contextEventService, "calculateScoreByQuestionType",
                eq(questionDataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));
        doReturn(createContextProfileEvent(contextProfileId, resourceId, "{}"))
                .when(contextEventService, "createContextProfileEvent", contextProfileId, previousResourceId);
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(contextService, times(1)).findByIdAndAssigneeId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findByCollectionId(any(UUID.class));
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateScoreByQuestionType",
                eq(questionDataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));
        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent",
                contextProfileId, previousResourceId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Test
    public void createContextProfileEventPrivateMethod() throws Exception {
        ContextProfileEvent result =
                WhiteboxImpl.invokeMethod(contextEventService, "createContextProfileEvent", contextProfileId, resourceId);

        assertEquals("Wrong contextProfileId", contextProfileId, result.getContextProfileId());
        assertEquals("Wrong profileId", resourceId, result.getResourceId());
    }

    @Test
    public void doOnResourceEventTransaction() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");

        WhiteboxImpl.invokeMethod(contextEventService, "doOnResourceEventTransaction",
                contextProfile, contextProfileEvent);

        verify(contextProfileService, times(1)).save(contextProfile);
        verify(contextProfileEventService, times(1)).save(contextProfileEvent);
    }

    @Test
    public void sendOnResourceEventMessage() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        PostRequestResourceDto postRequestResourceDto = createPostRequestResourceDto();

        WhiteboxImpl.invokeMethod(contextEventService, "sendOnResourceEventMessage",
                contextProfile, postRequestResourceDto, new EventSummaryDataDto());
        verify(activeMQClientService, times(1))
                .sendOnResourceEventMessage(eq(contextId), eq(profileId), any(OnResourceEventMessageDto.class));
    }

    @Test
    public void processFinishContextEventIsCompleteTrue() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(true);

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);

        contextEventService.processFinishContextEvent(contextId, profileId);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextService, times(0)).findById(any());
        verify(contextProfileEventService, times(0)).findByContextProfileId(any());
        verify(resourceService, times(0)).findByCollectionId(any());
        verifyPrivate(contextEventService, times(0)).invoke("createSkippedContextProfileEvents", any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("doFinishContextEventTransaction", any(), any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("sendFinishContextEventMessage", any(), any(), any());
    }

    @Test
    public void processFinishContextEvent() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        List<Resource> resources = Arrays.asList(createResource());
        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextService.findById(contextId)).thenReturn(context);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        when(resourceService.findByCollectionId(context.getCollectionId())).thenReturn(resources);
        doReturn(new ArrayList<Resource>()).when(contextEventService, "createSkippedContextProfileEvents",
                eq(contextProfileId), any(ArrayList.class));
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, true);
        doNothing().when(contextEventService, "doFinishContextEventTransaction",
                eq(contextProfile), eq(currentContextProfile), any(ArrayList.class));
        doNothing().when(contextEventService, "sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);

        contextEventService.processFinishContextEvent(contextId, profileId);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextService, times(1)).findById(contextId);
        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
        verify(resourceService, times(1)).findByCollectionId(context.getCollectionId());
        verifyPrivate(contextEventService, times(1)).invoke("createSkippedContextProfileEvents",
                eq(contextProfileId), any(ArrayList.class));
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, true);
        verifyPrivate(contextEventService, times(1)).invoke("doFinishContextEventTransaction",
                eq(contextProfile), eq(currentContextProfile), any(ArrayList.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);
    }

    @Test
    public void doFinishContextEventTransaction() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> eventsToCreate = new ArrayList<>();
        eventsToCreate.add(contextProfileEvent);
        eventsToCreate.add(contextProfileEvent);

        WhiteboxImpl.invokeMethod(contextEventService, "doFinishContextEventTransaction",
                contextProfile, currentContextProfile, eventsToCreate);

        verify(contextProfileService, times(1)).save(contextProfile);
        verify(contextProfileEventService, times(2)).save(contextProfileEvent);
    }

    @Test
    public void sendFinishContextEventMessage() throws Exception {
        WhiteboxImpl.invokeMethod(contextEventService, "sendFinishContextEventMessage",
                contextId, profileId, new EventSummaryDataDto());
        verify(activeMQClientService, times(1))
                .sendFinishContextEventMessage(eq(contextId), eq(profileId), any(FinishContextEventMessageDto.class));
    }

    @Test
    public void getContextEvents() throws Exception {
        //Map values for findAllContextEvents
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        List<AssigneeEventEntity> events = new ArrayList<>();

        //Body values
        UUID bodyResourceId = UUID.randomUUID(); //Last resource id
        long timeSpent = 1000;
        int reaction = 3;
        int score = 100;
        boolean isSkipped = false;
        String answerValue = "D";

        //Setting entity values
        AssigneeEventEntity assigneeEventEntity = Mockito.spy(AssigneeEventEntity.class);
        when(assigneeEventEntity.getCurrentResourceId()).thenReturn(resourceId);
        when(assigneeEventEntity.getEventData()).thenReturn(gson.toJson(createResponseResourceDto(bodyResourceId,
                score, reaction, timeSpent, createAnswerList(answerValue), isSkipped)));
        events.add(assigneeEventEntity);

        //Adding students
        UUID assigneeId1 = UUID.randomUUID();
        contextEventsMap.put(assigneeId1, events);

        //Setting context
        Context context = createContext();

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult1 = result.getProfileEvents().get(0);
        assertEquals("Wrong event size for assigneeId1", 1, profileResult1.getEvents().size());
        assertEquals("Wrong profile ID for assigneeId1", assigneeId1, profileResult1.getProfileId());
        assertEquals("Wrong current resource", resourceId, profileResult1.getCurrentResourceId());

        PostResponseResourceDto eventResult = profileResult1.getEvents().get(0);
        assertEquals("Wrong reaction", 3, eventResult.getReaction());
        assertEquals("Wrong timeSpent", 1000, eventResult.getTimeSpent());
        assertEquals("Wrong resource Id", bodyResourceId, eventResult.getResourceId());
        assertEquals("Wrong score", 100, eventResult.getScore());
        assertFalse("Wrong isSkipped value", eventResult.getIsSkipped());
        assertEquals("Wrong answer value", answerValue, eventResult.getAnswer().get(0).getValue());
    }

    @Test
    public void getContextEventsWithoutEvents() throws Exception {
        //Map values for findAllContextEvents
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        List<AssigneeEventEntity> events = new ArrayList<>();

        //Setting events
        UUID currentResourceId = UUID.randomUUID();

        //Setting entity values
        AssigneeEventEntity assigneeEventEntity = Mockito.spy(AssigneeEventEntity.class);
        when(assigneeEventEntity.getCurrentResourceId()).thenReturn(currentResourceId);
        when(assigneeEventEntity.getEventData()).thenReturn(null);
        events.add(assigneeEventEntity);

        //Adding student
        UUID assigneeId = UUID.randomUUID();
        contextEventsMap.put(assigneeId, events);

        //Setting context
        Context context = createContext();

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult = result.getProfileEvents().get(0);
        assertEquals("Wrong event size", 0, profileResult.getEvents().size());
        assertEquals("Wrong profile ID", assigneeId, profileResult.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult.getCurrentResourceId());
    }

    @Test
    public void calculateScoreByQuestionTypeTrueFalseRightAnswer() throws Exception {
        AnswerDto answer = createAnswerDto("A");

        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                trueFalseQuestion, userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeTrueFalseWrongAnswer() throws Exception {
        AnswerDto userAnswer = createAnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        AnswerDto correctAnswer = createAnswerDto("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                trueFalseQuestion, userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceRightAnswer() throws Exception {
        AnswerDto answer = new AnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType", singleChoiceQuestion,
                userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceWrongAnswer() throws Exception {
        AnswerDto correctAnswer = createAnswerDto("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        AnswerDto userAnswer = createAnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                singleChoiceQuestion, userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreForSimpleOptionRightAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "a";

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer,
                correctAnswer);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreForSimpleOptionWrongAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "B";

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer,
                correctAnswer);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateEventSummaryDataSkipTrue() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = createContextProfileEvents();

        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, true);

        assertNotNull("ContextProfile EventSummaryData is null", eventSummaryDataDto);
        assertEquals(eventSummaryDataDto.getAverageScore(), 60);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 50);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 2);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 3);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 5);
    }

    @Test
    public void calculateEventSummaryDataSkipFalse() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = createContextProfileEvents();

        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, false);

        assertNotNull("ContextProfile EventSummaryData is null", eventSummaryDataDto);
        assertEquals(eventSummaryDataDto.getAverageScore(), 75);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 50);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 2);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 3);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 4);
    }

    @Test
    public void calculateEventSummaryDataForEmptyEventList() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, false);

        assertEquals(eventSummaryDataDto.getAverageScore(), 0);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 0);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 0);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 0);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 0);
    }

    private List<ContextProfileEvent> createContextProfileEvents() {
        ContextProfileEvent event1 = createContextProfileEvent(contextProfileId, UUID.randomUUID(), gson.toJson(createResponseResourceDto(UUID.randomUUID(), 100, 2, 10,
                createAnswerList("A"), false)));

        ContextProfileEvent event2 = createContextProfileEvent(contextProfileId, UUID.randomUUID(), gson.toJson(createResponseResourceDto(UUID.randomUUID(), 100, 1, 16,
                createAnswerList("B"), false)));

        ContextProfileEvent event3 = createContextProfileEvent(contextProfileId, UUID.randomUUID(), gson.toJson(createResponseResourceDto(UUID.randomUUID(), 0, 5, 15,
                createAnswerList("C"), false)));

        List<AnswerDto> answerDtos = new ArrayList<>();
        ContextProfileEvent event4 = createContextProfileEvent(contextProfileId, UUID.randomUUID(), gson.toJson(createResponseResourceDto(UUID.randomUUID(), 0, 0, 1,
                answerDtos, true)));

        ContextProfileEvent event5 = createContextProfileEvent(contextProfileId, UUID.randomUUID(), gson.toJson(createResponseResourceDto(UUID.randomUUID(), 100, 3, 8,
                createAnswerList("value"), false)));

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(event1);
        contextProfileEvents.add(event2);
        contextProfileEvents.add(event3);
        contextProfileEvents.add(event4);
        contextProfileEvents.add(event5);

        return contextProfileEvents;
    }

    private Context createContext() {
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);
        context.setContextData("{}");
        return context;
    }

    private ContextProfile createContextProfile() {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        contextProfile.setProfileId(profileId);
        contextProfile.setContextId(contextId);
        return contextProfile;
    }

    private CurrentContextProfile createCurrentContextProfile() {
        currentContextProfile.setContextId(contextId);
        currentContextProfile.setProfileId(profileId);
        currentContextProfile.setContextProfileId(contextProfileId);
        return currentContextProfile;
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

    private ContextProfileEvent createContextProfileEvent(UUID contextProfileId, UUID resourceId, String evenData) {
        ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
        contextProfileEvent.setId(UUID.randomUUID());
        contextProfileEvent.setContextProfileId(contextProfileId);
        contextProfileEvent.setResourceId(resourceId);
        contextProfileEvent.setEventData(evenData);
        return contextProfileEvent;
    }

    private Resource createResource() {
        Resource resource = new Resource();
        resource.setId(resourceId);
        return resource;
    }

    private StartContextEventResponseDto createStartContextEventResponseDto() {
        StartContextEventResponseDto startContextDto = new StartContextEventResponseDto();
        startContextDto.setId(startContextId);
        startContextDto.setEvents(new ArrayList<>());
        startContextDto.setCollection(new CollectionDto(collectionId.toString()));
        startContextDto.setCurrentResourceId(resourceId);
        return startContextDto;
    }

    private AnswerDto createAnswerDto(String answer) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setValue(answer);
        return answerDto;
    }

    private List<AnswerDto> createAnswerList(String answer) {
        List<AnswerDto> answers = new ArrayList<>();
        AnswerDto answerDto = new AnswerDto(answer);
        answers.add(answerDto);
        return answers;
    }

    private PostRequestResourceDto createPostRequestResourceDto() {
        PostRequestResourceDto postRequestResourceDto = new PostRequestResourceDto();

        postRequestResourceDto.setIsSkipped(false);
        postRequestResourceDto.setScore(100);
        postRequestResourceDto.setResourceId(resourceId);
        postRequestResourceDto.setReaction(5);
        postRequestResourceDto.setTimeSpent(1000);

        List<AnswerDto> answerDtos = new ArrayList<>();
        answerDtos.add(createAnswerDto("A"));

        postRequestResourceDto.setAnswer(answerDtos);

        return postRequestResourceDto;
    }

    private OnResourceEventPostRequestDto createOnResourceEventPostRequestDto() {
        OnResourceEventPostRequestDto body = new OnResourceEventPostRequestDto();
        PostRequestResourceDto postRequestResourceDto = createPostRequestResourceDto();
        postRequestResourceDto.setResourceId(previousResourceId);
        body.setPreviousResource(postRequestResourceDto);
        return body;
    }

    private QuestionDataDto createQuestionDataDto(List<AnswerDto> answers, String questionType) {
        QuestionDataDto questionDataDto = new QuestionDataDto();
        questionDataDto.setCorrectAnswer(answers);
        questionDataDto.setType(questionType);
        return questionDataDto;
    }

}