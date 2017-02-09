package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.junit.Assert;
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
import java.util.List;
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
    private String token;
    private UUID contextId;
    private UUID resourceId;
    private UUID previousResourceId;
    private UUID contextProfileId;
    private UUID ownerId;
    private UUID profileId;
    private CurrentContextProfile currentContextProfile;

    @Before
    public void beforeEachTest() {
        collectionId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        contextId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        previousResourceId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentContextProfile = new CurrentContextProfile();
    }

    @Test
    public void processStartContextEventWithContextProfileIsCompleteTrue() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        when(entity.getIsComplete()).thenReturn(true);
        when(entity.getCurrentContextProfileId()).thenReturn(contextProfileId);


        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(entity);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(contextId,
                profileId);
        verifyPrivate(contextEventService, times(1)).invoke("createContextProfile", entity);
        verifyPrivate(contextEventService, times(0)).invoke("resumeStartContextEvent", any());
        verifyPrivate(contextEventService, times(0)).invoke("createCurrentContextProfile", any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContextEventWithContextProfileIsCompleteFalse() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        when(entity.getIsComplete()).thenReturn(false);
        when(entity.getCurrentContextProfileId()).thenReturn(contextProfileId);

        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(entity);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);
        verifyPrivate(contextEventService, times(0)).invoke("createContextProfile", entity);
        verifyPrivate(contextEventService, times(1)).invoke("resumeStartContextEvent", any());
        verifyPrivate(contextEventService, times(0)).invoke("createCurrentContextProfile", any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContextEventWithoutCurrentContextProfile() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();

        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId,
                profileId)).thenReturn(entity);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(contextId,
                profileId);
        verifyPrivate(contextEventService, times(0)).invoke("createContextProfile", entity);
        verifyPrivate(contextEventService, times(0)).invoke("resumeStartContextEvent", any());
        verifyPrivate(contextEventService, times(1)).invoke("createCurrentContextProfile", any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void createContextProfilePrivateMethod() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        ContextProfile contextProfile = createContextProfile();

        doReturn(contextProfile).when(contextEventService, "createContextProfileObject", contextId, profileId);
        doNothing().when(contextEventService, "doCreateContextProfileTransaction", contextProfile);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "processStartContext",
                eq(entity), any(ArrayList.class));

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "createContextProfile", entity);

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileObject", contextId, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("doCreateContextProfileTransaction", contextProfile);
        verifyPrivate(contextEventService, times(1)).invoke("processStartContext", eq(entity), any(ArrayList.class));
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void createCurrentContextProfilePrivateMethod() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();

        doReturn(currentContextProfile).when(contextEventService, "createCurrentContextProfileObject",
                contextId, profileId, contextProfileId);
        doNothing().when(contextEventService, "doCurrentContextEventTransaction", currentContextProfile);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "processStartContext",
                eq(entity), any(ArrayList.class));

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "createCurrentContextProfile", entity);

        verifyPrivate(contextEventService, times(1))
                .invoke("createCurrentContextProfileObject", contextId, profileId, contextProfileId);
        verifyPrivate(contextEventService, times(1)).invoke("doCurrentContextEventTransaction", currentContextProfile);
        verifyPrivate(contextEventService, times(1)).invoke("processStartContext", eq(entity), any(ArrayList.class));
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContext() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        when(entity.getCurrentResourceId()).thenReturn(resourceId);

        doNothing().when(contextEventService, "sendStartEventMessage",
                contextId, profileId, resourceId, true);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                eq(contextId), eq(resourceId), eq(collectionId), any(ArrayList.class));

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "processStartContext", entity, new ArrayList<>());

        verifyPrivate(contextEventService, times(1)).invoke("prepareStartContextEventResponse", eq(contextId),
                eq(resourceId), eq(collectionId), any(ArrayList.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendStartEventMessage",
                contextId, profileId, resourceId, true);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void createCurrentContextProfileObject() throws Exception {
        CurrentContextProfile result = WhiteboxImpl.invokeMethod(contextEventService,
                "createCurrentContextProfileObject", contextId, profileId, contextProfileId);

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
        assertEquals("Wrong contextProfile ID", contextProfileId, result.getContextProfileId());
    }

    @Test
    public void createContextProfileObject() throws Exception {
        ContextProfile result = WhiteboxImpl.invokeMethod(contextEventService,
                "createContextProfileObject", contextId, profileId);

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertFalse("IsComplete is true", result.getIsComplete());

        EventSummaryDataDto eventResult = gson.fromJson(result.getEventSummaryData(), EventSummaryDataDto.class);
        Assert.assertEquals("Wrong total time", 0, eventResult.getTotalTimeSpent());
        Assert.assertEquals("Wrong average reaction", 0, eventResult.getAverageReaction());
        Assert.assertEquals("Wrong average score", 0, eventResult.getAverageScore());
        Assert.assertEquals("Wrong total answered", 0, eventResult.getTotalAnswered());
        Assert.assertEquals("Wrong total correct", 0, eventResult.getTotalCorrect());
    }

    @Test
    public void doCreateContextProfileTransaction() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();

        when(contextProfileService.save(contextProfile)).thenReturn(contextProfile);
        doNothing().when(contextEventService, "doCurrentContextEventTransaction", currentContextProfile);
        doReturn(currentContextProfile).when(contextEventService, "createCurrentContextProfileObject",
                contextId, profileId, contextProfileId);

        WhiteboxImpl.invokeMethod(contextEventService, "doCreateContextProfileTransaction", contextProfile);

        verifyPrivate(contextEventService, times(1)).invoke("createCurrentContextProfileObject", contextId,
                profileId, contextProfileId);
        verify(contextProfileService, times(1)).save(contextProfile);
        verifyPrivate(contextEventService, times(1)).invoke("doCurrentContextEventTransaction", currentContextProfile);
    }

    @Test
    public void doCurrentContextEventTransaction() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();

        WhiteboxImpl.invokeMethod(contextEventService, "doCurrentContextEventTransaction", currentContextProfile);

        verify(currentContextProfileService, times(1)).delete(currentContextProfile);
        verify(currentContextProfileService, times(1)).create(currentContextProfile);
    }

    @Test
    public void sendStartEventMessage() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();
        when(entity.getIsComplete()).thenReturn(true);
        when(entity.getCurrentResourceId()).thenReturn(resourceId);

        WhiteboxImpl.invokeMethod(contextEventService, "sendStartEventMessage", contextId, profileId, resourceId, true);

        verify(activeMQClientService, times(1)).sendStartContextEventMessage(eq(contextId), eq(profileId), any());
    }

    @Test
    public void prepareStartContextEventResponse() throws Exception {
        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "prepareStartContextEventResponse",
                        contextId, resourceId, collectionId, new ArrayList<>());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong currentResourceId", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong events size", 0, result.getEvents().size());
    }

    @Test
    public void resumeStartContextEvent() throws Exception {
        ContextProfileEntity entity = createContextProfileEntity();

        doReturn(createStartContextEventResponseDto()).when(contextEventService, "processStartContext",
                eq(entity), any(ArrayList.class));

        StartContextEventResponseDto result =
                WhiteboxImpl.invokeMethod(contextEventService, "resumeStartContextEvent", entity);

        verifyPrivate(contextEventService, times(1)).invoke("processStartContext", eq(entity), any(ArrayList.class));
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processOnResourceEventWithoutEvent() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionResources).when(contextEventService, "getCollectionResources", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionResources,
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionResources,
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
                contextProfileId, previousResourceId);
        doReturn(100).when(contextEventService, "calculateScore", previousResource,
                body.getPreviousResource().getAnswer());

        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(contextEventService, times(1)).invoke("getCollectionResources", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent", contextProfileId,
                previousResourceId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateScore", previousResource,
                body.getPreviousResource().getAnswer());

        verifyPrivate(contextEventService, times(0)).invoke("updateExistingResourceDto", any(), any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    public void processOnResourceEventWithoutEventWithoutAnswer() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        body.getPreviousResource().getAnswer().remove(0);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionResources).when(contextEventService, "getCollectionResources", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionResources,
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionResources,
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
                contextProfileId, previousResourceId);

        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(contextEventService, times(1)).invoke("getCollectionResources", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent", contextProfileId,
                previousResourceId);
        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", any(), any());

        verifyPrivate(contextEventService, times(0)).invoke("updateExistingResourceDto", any(), any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Test
    public void processOnResourceEventExistingEvent() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = Arrays.asList(contextProfileEvent);
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionResources).when(contextEventService, "getCollectionResources", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionResources,
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionResources,
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(body.getPreviousResource()).when(contextEventService, "updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(contextEventService, times(1)).invoke("getCollectionResources", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(0)).invoke("createContextProfileEvent", any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", any(), any());

        verifyPrivate(contextEventService, times(1)).invoke("updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", contextProfileId, resourceId,
                gson.toJson(eventSummaryDataDto));
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
        assertEquals("Wrong resource id", resourceId, result.getResourceId());
    }

    @Test
    public void calculateScore() throws Exception {
        List<AnswerDto> answerDtos = new ArrayList<>();
        answerDtos.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = new ResourceMetadataDto();
        resourceMetadataDto.setCorrectAnswer(answerDtos);
        resourceMetadataDto.setType("true_false");

        ResourceDto resource = createResourceDto();
        resource.setMetadata(resourceMetadataDto);

        doReturn(100).when(contextEventService, "calculateScoreByQuestionType", eq(resourceMetadataDto.getType()),
                eq(answerDtos), any(List.class));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScore", resource, answerDtos);

        verifyPrivate(contextEventService, times(1)).invoke("calculateScoreByQuestionType",
                eq(resourceMetadataDto.getType()), eq(answerDtos), any(List.class));

        assertEquals("Wrong score", 100, result);
    }

    @Test
    public void updateExistingResourceDtoSkipTrueAndOldResourceSkipTrue() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        eventData.setIsSkipped(true);
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        resource.setIsSkipped(true);
        ResourceDto resourceInfo = createResourceDto();

        PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
                contextProfileEvent, resourceInfo, resource);

        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        assertEquals("Wrong score", 100, result.getScore());
        assertEquals("Wrong reaction", 5, result.getReaction());
    }

    @Test
    public void updateExistingResourceDtoSkipTrue() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        resource.setIsSkipped(true);
        resource.setReaction(1);
        ResourceDto resourceInfo = createResourceDto();

        PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
                contextProfileEvent, resourceInfo, resource);

        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        assertEquals("Wrong score", 100, result.getScore());
        assertEquals("Wrong reaction", 1, result.getReaction());
    }

    @Test
    public void updateExistingResourceDtoSkipFalse() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        ResourceDto resourceInfo = createResourceDto();

        doReturn(0).when(contextEventService, "calculateScore", resourceInfo, resource.getAnswer());

        PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
                contextProfileEvent, resourceInfo, resource);

        verifyPrivate(contextEventService, times(1)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        assertEquals("Wrong score", 0, result.getScore());
        assertEquals("Wrong reaction", 5, result.getReaction());
    }

    @Test
    public void findResourceInContext() throws Exception {
        ResourceDto resource = createResourceDto();

        List<ResourceDto> collectionResources = new ArrayList<>();
        collectionResources.add(resource);

        ResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "findResourceInContext",
                collectionResources, resourceId, contextId);

        assertEquals("Wrong resource ID", resourceId, result.getId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findResourceInContextThrowException() throws Exception {
        ResourceDto resource = createResourceDto();

        List<ResourceDto> collectionResources = new ArrayList<>();
        collectionResources.add(resource);

        WhiteboxImpl.invokeMethod(contextEventService, "findResourceInContext",
                collectionResources, previousResourceId, contextId);
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

        contextEventService.processFinishContextEvent(contextId, profileId, token);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextService, times(0)).findById(any());
        verifyPrivate(contextEventService, times(0)).invoke("finishContextEvent", any(), any());
    }

    @Test
    public void processFinishContextEventIsCompleteFalse() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);
        Context context = createContext();

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextService.findById(contextId)).thenReturn(context);
        doNothing().when(contextEventService, "finishContextEvent", context, contextProfile);

        contextEventService.processFinishContextEvent(contextId, profileId, token);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextService, times(1)).findById(contextId);
        verifyPrivate(contextEventService, times(1)).invoke("finishContextEvent", context, contextProfile);
    }

    @Test
    public void finishContextEvent() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        List<ResourceContentDto> resources = new ArrayList<>();

        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        doReturn(resources).when(contextEventService, "getCollectionResources",
                collectionId, true);
        doReturn(resources).when(contextEventService, "getResourcesToCreate", contextProfileEvents, resources);
        doReturn(contextProfileEvents).when(contextEventService, "createSkippedContextProfileEvents",
                contextProfileId, resources);
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, true);
        doNothing().when(contextEventService, "doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        doNothing().when(contextEventService, "sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);

        WhiteboxImpl.invokeMethod(contextEventService, "finishContextEvent", context, contextProfile);

        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
        verifyPrivate(contextEventService, times(1)).invoke("getCollectionResources", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("getResourcesToCreate", contextProfileEvents, resources);
        verifyPrivate(contextEventService, times(1)).invoke("createSkippedContextProfileEvents",
                contextProfileId, resources);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, true);
        verifyPrivate(contextEventService, times(1)).invoke("doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        verifyPrivate(contextEventService, times(1)).invoke("sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);
    }

    @Test
    public void createSkippedContextProfileEvents() throws Exception {
        ResourceDto resource1 = new ResourceDto();
        resource1.setId(resourceId);

        ResourceDto resource2 = new ResourceDto();
        resource2.setId(UUID.randomUUID());

        doReturn(new PostResponseResourceDto()).when(contextEventService, "createSkippedEventData", any(UUID.class));

        List<ContextProfileEvent> result = WhiteboxImpl.invokeMethod(contextEventService,
                "createSkippedContextProfileEvents", contextProfileId, Arrays.asList(resource1, resource2));

        verifyPrivate(contextEventService, times(2)).invoke("createSkippedEventData", any(UUID.class));

        assertEquals("Wrong size", 2, result.size());
        assertEquals("Wrong resourceToCrete ID", resourceId, result.get(0).getResourceId());
        assertEquals("Wrong contextProfile ID", contextProfileId, result.get(0).getContextProfileId());
        assertNotNull("EventData is null", result.get(0).getEventData());

    }

    @Test
    public void createSkippedEventData() throws Exception {
        PostResponseResourceDto result = WhiteboxImpl.invokeMethod(contextEventService,
                "createSkippedEventData", resourceId);

        assertEquals("Wrong score", 0, result.getScore());
        assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        assertEquals("Wrong reaction", 0, result.getReaction());
        assertTrue("Skip is false", result.getIsSkipped());
        assertEquals("Wrong timespent", 0, result.getTimeSpent());
        assertNull("Answer is not null", result.getAnswer());
    }


    @Test
    public void getResourcesToCreateOneResult() throws Exception {
        UUID commonResourceId = UUID.randomUUID();
        ResourceDto resource1 = new ResourceDto();
        resource1.setId(commonResourceId);

        ResourceDto resource2 = new ResourceDto();
        resource2.setId(resourceId);

        List<ContextProfileEvent> contextProfileEvents =
                Arrays.asList(createContextProfileEvent(contextProfileId, commonResourceId, "{}"));

        List<ResourceDto> result = WhiteboxImpl.invokeMethod(contextEventService, "getResourcesToCreate",
                contextProfileEvents, Arrays.asList(resource1, resource2));

        assertEquals("Wrong size", 1, result.size());
        assertEquals("Wrong resourceToCrete ID", resourceId, result.get(0).getId());
    }

    @Test
    public void getResourcesToCreateZeroResults() throws Exception {
        ResourceDto resource = new ResourceDto();
        resource.setId(resourceId);

        List<ContextProfileEvent> contextProfileEvents =
                Arrays.asList(createContextProfileEvent(contextProfileId, resourceId, "{}"));

        List<ResourceContentDto> result = WhiteboxImpl.invokeMethod(contextEventService, "getResourcesToCreate",
                contextProfileEvents, Arrays.asList(resource));

        assertEquals("Wrong size", 0, result.size());
    }

    @Test
    public void getCollectionResourcesForCollectionType() throws Exception {
        when(collectionService.getCollectionResources(collectionId.toString()))
                .thenReturn(new ArrayList<ResourceDto>());

        WhiteboxImpl.invokeMethod(contextEventService, "getCollectionResources", collectionId, true);

        verify(collectionService, times(1)).getCollectionResources(collectionId.toString());
        verify(collectionService, times(0)).getAssessmentQuestions(collectionId.toString());
    }

    @Test
    public void getCollectionResourcesForAssessmentType() throws Exception {
        when(collectionService.getAssessmentQuestions(collectionId.toString()))
                .thenReturn(new ArrayList<ResourceDto>());

        WhiteboxImpl.invokeMethod(contextEventService, "getCollectionResources", collectionId, false);

        verify(collectionService, times(0)).getCollectionResources(collectionId.toString());
        verify(collectionService, times(1)).getAssessmentQuestions(collectionId.toString());
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
                contextProfile, eventsToCreate);

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
    public void calculateScoreByQuestionTypeTrueFalseRightAnswer() throws Exception {
        AnswerDto answer = createAnswerDto("A");

        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.TrueFalse.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeTrueFalseWrongAnswer() throws Exception {
        AnswerDto userAnswer = createAnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        AnswerDto correctAnswer = createAnswerDto("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.TrueFalse.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceRightAnswer() throws Exception {
        AnswerDto answer = new AnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.SingleChoice.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceWrongAnswer() throws Exception {
        AnswerDto correctAnswer = createAnswerDto("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        AnswerDto userAnswer = createAnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.SingleChoice.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreForSimpleOptionRightAnswer() throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("a"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswers,
                correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreForSimpleOptionWrongAnswer() throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("B"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswers,
                correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreByQuestionTypeDragAndDropRightAnswer() throws Exception {
        calculateScoreForOrderedMultipleChoiceRightAnswers(QuestionTypeEnum.DragAndDrop);
    }

    @Test
    public void calculateScoreByQuestionTypeDragAndDropWrongAnswer() throws Exception {
        calculateScoreForOrderedMultipleChoiceWrongAnswers(QuestionTypeEnum.DragAndDrop);
    }

    @Test
    public void calculateScoreByQuestionTypeDragAndDropWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.DragAndDrop);
    }

    @Test
    public void calculateScoreForTextEntryRightAnswers() throws Exception {
        calculateScoreForOrderedMultipleChoiceRightAnswers(QuestionTypeEnum.TextEntry);
    }

    @Test
    public void calculateScoreForTextEntryCaseInsensitiveRightAnswers() throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("ONE"), createAnswerDto(" two "), createAnswerDto("THRee "));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("One"), createAnswerDto("Two"), createAnswerDto("Three"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.TextEntry.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreForMultipleChoiceRightAnswersForEmptyArrays() throws Exception {
        List<AnswerDto> userAnswers = new ArrayList<>();
        List<AnswerDto> correctAnswers = new ArrayList<>();

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.MultipleChoice.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    public void calculateScoreForTextEntryWrongAnswers() throws Exception {
        calculateScoreForOrderedMultipleChoiceWrongAnswers(QuestionTypeEnum.TextEntry);
    }

    @Test
    public void calculateScoreForTextEntryWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.TextEntry);
    }

    @Test
    public void calculateScoreForMultipleChoiceRightAnswers() throws Exception {
        calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum.MultipleChoice);
    }

    @Test
    public void calculateScoreForMultipleChoiceWrongAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum.MultipleChoice);
    }

    @Test
    public void calculateScoreForMultipleChoiceWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.MultipleChoice);
    }

    @Test
    public void calculateScoreForMultipleChoiceImageRightAnswers() throws Exception {
        calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum.MultipleChoiceImage);
    }

    @Test
    public void calculateScoreForMultipleChoiceImageWrongAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum.MultipleChoiceImage);
    }

    @Test
    public void calculateScoreForMultipleChoiceImageWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.MultipleChoiceImage);
    }

    @Test
    public void calculateScoreForMultipleChoiceTextRightAnswers() throws Exception {
        calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum.MultipleChoiceText);
    }

    @Test
    public void calculateScoreForMultipleChoiceTextWrongAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum.MultipleChoiceText);
    }

    @Test
    public void calculateScoreForMultipleChoiceTextWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.MultipleChoiceText);
    }

    @Test
    public void calculateScoreForHotTextWordRightAnswers() throws Exception {
        calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum.HotTextWord);
    }

    @Test
    public void calculateScoreForHotTextWordWrongAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum.HotTextWord);
    }

    @Test
    public void calculateScoreForHotTextWordWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.HotTextWord);
    }

    @Test
    public void calculateScoreForHotTextSentenceRightAnswers() throws Exception {
        calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum.HotTextSentence);
    }

    @Test
    public void calculateScoreForHotTextSentenceWrongAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum.HotTextSentence);
    }

    @Test
    public void calculateScoreForHotTextSentenceWrongNumberOfAnswers() throws Exception {
        calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum.HotTextSentence);
    }

    @Test
    public void calculateScoreForExtendedText() throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("Open text answer..."));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                QuestionTypeEnum.ExtendedText.getLiteral(), userAnswers, null);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateEventSummaryDataSkipTrue() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = createContextProfileEvents();

        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary", contextProfileEvents, true);

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
                "calculateEventSummary", contextProfileEvents, false);

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
                "calculateEventSummary", contextProfileEvents, false);

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
        context.setIsCollection(true);
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

    private StartContextEventResponseDto createStartContextEventResponseDto() {
        StartContextEventResponseDto startContextDto = new StartContextEventResponseDto();
        startContextDto.setContextId(contextId);
        startContextDto.setCollectionId(collectionId);
        startContextDto.setEvents(new ArrayList<>());
        return startContextDto;
    }

    private AnswerDto createAnswerDto(String answer) {
        AnswerDto answerDto = new AnswerDto(answer);
        answerDto.setValue(answer);
        return answerDto;
    }

    private ResourceDto createResourceDto() {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resourceId);
        return resourceDto;
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

    private ResourceMetadataDto createQuestionDataDto(List<AnswerDto> answers, String questionType) {
        ResourceMetadataDto resourceMetadataDto = new ResourceMetadataDto();
        resourceMetadataDto.setCorrectAnswer(answers);
        resourceMetadataDto.setType(questionType);
        return resourceMetadataDto;
    }

    private AssignedContextEntity createAssignedContextEntity() {
        AssignedContextEntity entity = mock(AssignedContextEntity.class);

        when(entity.getContextId()).thenReturn(contextId);
        when(entity.getCollectionId()).thenReturn(collectionId);
        when(entity.getProfileId()).thenReturn(profileId);
        when(entity.getContextProfileId()).thenReturn(contextProfileId);

        return entity;
    }

    private ContextProfileEntity createContextProfileEntity() {
        ContextProfileEntity entity = mock(ContextProfileEntity.class);

        when(entity.getContextId()).thenReturn(contextId);
        when(entity.getCollectionId()).thenReturn(collectionId);
        when(entity.getProfileId()).thenReturn(profileId);
        when(entity.getContextProfileId()).thenReturn(contextProfileId);

        return entity;
    }

    private void calculateScoreForMultipleOptionRightAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"), createAnswerDto("B"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("B"), createAnswerDto("A"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    private void calculateScoreForMultipleOptionWrongAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"), createAnswerDto("B"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("B"), createAnswerDto("C"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    private void calculateScoreForMultipleOptionWrongNumberOfAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("B"), createAnswerDto("A"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    private void calculateScoreForOrderedMultipleChoiceRightAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"), createAnswerDto("B"), createAnswerDto("B"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("A"), createAnswerDto("B"), createAnswerDto("B"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    private void calculateScoreForOrderedMultipleChoiceWrongAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("A"), createAnswerDto("B"), createAnswerDto("B"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("B"), createAnswerDto("A"), createAnswerDto("B"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

}