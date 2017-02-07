package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ContextEventsResponseDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileEventResponseDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextProfileWithContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.junit.Assert.assertNull;
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
    private Gson gson = new Gson();

    @Mock
    private JsonParser jsonParser;

    private UUID collectionId;
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
        when(entity.getIsComplete()).thenReturn(true);

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(contextService.findProfileIdInContext(contextId, profileId)).thenReturn(entity);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findProfileIdInContext(contextId, profileId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
        when(entity.getIsComplete()).thenReturn(false);

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(contextService.findProfileIdInContext(contextId, profileId)).thenReturn(entity);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findProfileIdInContext(contextId, profileId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();

        StartContextEventResponseDto startContextEventResponseDto = createStartContextEventResponseDto();

        when(contextService.findProfileIdInContext(contextId, profileId)).thenReturn(entity);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenThrow(ContentNotFoundException.class);

        doReturn(startContextEventResponseDto)
                .when(contextEventService, "createContextProfile", entity);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findProfileIdInContext(contextId, profileId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
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

    @Ignore
    @Test
    public void findFirstResourceByContextId() throws Exception {
        //Resource result =
        WhiteboxImpl.invokeMethod(contextEventService, "findFirstResourceByContextId", contextId);

        //assertEquals("Wrong resourceId", resourceId, result.getId());
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();
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
        ContextProfileWithContextEntity entity = createContextProfileContextEntity();

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

    @Ignore
    @Test
    public void processOnResourceEventWithoutAnswer() throws Exception {
        //Resource resource = createResource();
        //Resource previousResource = createResource();
        //previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        //previousResource.setResourceData(gson.toJson(questionMetadataDto));

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        body.getPreviousResource().getAnswer().remove(0);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);
        //collectionResources.add(previousResource);
        contextProfileEvents.add(contextProfileEvent);
        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);
        //collectionResources.add(previousResource);

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
                currentContextProfile.getContextProfileId(), previousResourceId);
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);

        //verifyPrivate(contextEventService, times(0))
        //        .invoke("calculateScore", previousResource, body.getPreviousResource().getAnswer());
        verifyPrivate(contextEventService, times(0)).invoke("updateExistingResourceDto", any(), any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent",
                currentContextProfile.getContextProfileId(), previousResourceId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Ignore
    @Test
    public void processOnResourceEventWithAnswer() throws Exception {
        //Resource resource = createResource();

        //Resource previousResource = createResource();
        //previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));
        //QuestionDataDto questionDataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        //previousResource.setResourceData(gson.toJson(questionDataDto));
        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        //previousResource.setResourceData(gson.toJson(questionDataDto));

        //EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);
        //collectionResources.add(previousResource);

        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);
        //collectionResources.add(previousResource);

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(100).when(contextEventService, "calculateScoreByQuestionType",
                eq(resourceMetadataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));
//        doReturn(createContextProfileEvent(contextProfileId, resourceId, "{}"))
//                eq(questionMetadataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));
        //doReturn(createContextProfileEvent(contextProfileId, resourceId, "{}"))
        //        eq(questionMetadataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));

        doReturn(createContextProfileEvent(contextProfileId, resourceId, "{}"))
                .when(contextEventService, "createContextProfileEvent", contextProfileId, previousResourceId);
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        //doReturn(100).when(contextEventService, "calculateScore",
        //        previousResource, body.getPreviousResource().getAnswer());
        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
                currentContextProfile.getContextProfileId(), previousResourceId);
        doNothing().when(contextEventService, "doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);

        //verifyPrivate(contextEventService, times(1))
        //        .invoke("calculateScore", previousResource, body.getPreviousResource().getAnswer());
        verifyPrivate(contextEventService, times(0)).invoke("updateExistingResourceDto", any(), any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("calculateScoreByQuestionType",
                eq(resourceMetadataDto.getType()), eq(body.getPreviousResource().getAnswer()), any(AnswerDto.class));
        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent",
                currentContextProfile.getContextProfileId(), previousResourceId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
                eq(contextProfile), any(ContextProfileEvent.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Ignore
    @Test
    public void processOnResourceEventExistingEventWithAnswer() throws Exception {
        //Resource resource = createResource();
        //Resource previousResource = createResource();
        //previousResource.setId(previousResourceId);

//        List<AnswerDto> answers = new ArrayList<>();
//        answers.add(createAnswerDto("A"));
        //QuestionDataDto questionDataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        //previousResource.setResourceData(gson.toJson(questionDataDto));

//        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
//        ContextProfile contextProfile = createContextProfile();
//        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

//        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

//        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
//        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
//        contextProfileEvents.add(contextProfileEvent);
        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);
        //collectionResources.add(previousResource);

//        when(contextService.findByIdAndAssigneeId(any(UUID.class), any(UUID.class))).thenReturn(new Context());
//        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
//                .thenReturn(currentContextProfile);
//        //when(resourceService.findByCollectionId(any(UUID.class))).thenReturn(collectionResources);
//        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
//        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
//
//        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        //doReturn(body.getPreviousResource()).when(contextEventService, "updateExistingResourceDto", contextProfileEvent,
        //        previousResource, body.getPreviousResource());
//        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
//                currentContextProfile.getContextProfileId(), previousResourceId);
//        doNothing().when(contextEventService, "doOnResourceEventTransaction",
//                eq(contextProfile), any(ContextProfileEvent.class));
//        doNothing().when(contextEventService, "sendOnResourceEventMessage",
//                eq(contextProfile), eq(body.getPreviousResource()), any(EventSummaryDataDto.class));

        //contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

//        verify(contextService, times(1)).findByIdAndAssigneeId(any(UUID.class), any(UUID.class));
//        //verify(resourceService, times(1)).findByCollectionId(any(UUID.class));
//        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
//        verify(contextProfileService, times(1)).findById(contextProfileId);
//        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
//
//        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", any(), any());
        //verifyPrivate(contextEventService, times(1)).invoke("updateExistingResourceDto", contextProfileEvent,
        //        previousResource, body.getPreviousResource());
//        verifyPrivate(contextEventService, times(0)).invoke("createContextProfileEvent", any(), any());
//        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
//        verifyPrivate(contextEventService, times(1)).invoke("doOnResourceEventTransaction",
//                eq(contextProfile), any(ContextProfileEvent.class));
//        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
//                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
    }

    @Ignore
    @Test
    public void createContextProfileEventPrivateMethod() throws Exception {
        ContextProfileEvent result =
                WhiteboxImpl.invokeMethod(contextEventService, "createContextProfileEvent", contextProfileId, resourceId);

        assertEquals("Wrong contextProfileId", contextProfileId, result.getContextProfileId());
        assertEquals("Wrong resource id", resourceId, result.getResourceId());
    }

    @Ignore
    @Test
    public void calculateScore() throws Exception {
        List<AnswerDto> answerDtos = new ArrayList<>();
        answerDtos.add(createAnswerDto("A"));

        //QuestionDataDto questionDataDto = new QuestionDataDto();
        //questionDataDto.setCorrectAnswer(answerDtos);
        //questionDataDto.setType("true_false");

        //Resource resource = createResource();
        //resource.setResourceData(gson.toJson(questionDataDto));

        //doReturn(100).when(contextEventService, "calculateScoreByQuestionType", eq(questionDataDto.getType()),
        //        eq(answerDtos), any(List.class));

        //int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScore", resource, answerDtos);

        //verifyPrivate(contextEventService, times(1)).invoke("calculateScoreByQuestionType",
        //        eq(questionDataDto.getType()), eq(answerDtos), any(List.class));

        //assertEquals("Wrong score", 100, result);
    }

    @Ignore
    @Test
    public void updateExistingResourceDtoSkipTrueAndOldResourceSkipTrue() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        eventData.setIsSkipped(true);
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        resource.setIsSkipped(true);
        //Resource resourceInfo = createResource();

        //PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
        //        contextProfileEvent, resourceInfo, resource);

        //verifyPrivate(contextEventService, times(0)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        //assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        //assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        //assertEquals("Wrong score", 100, result.getScore());
        //assertEquals("Wrong reaction", 5, result.getReaction());
    }

    @Ignore
    @Test
    public void updateExistingResourceDtoSkipTrue() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        resource.setIsSkipped(true);
        resource.setReaction(1);
        //Resource resourceInfo = createResource();

        //PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
        //        contextProfileEvent, resourceInfo, resource);

        //verifyPrivate(contextEventService, times(0)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        //assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        //assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        //assertEquals("Wrong score", 100, result.getScore());
        //assertEquals("Wrong reaction", 1, result.getReaction());
    }

    @Ignore
    @Test
    public void updateExistingResourceDtoSkipFalse() throws Exception {
        PostRequestResourceDto eventData = createPostRequestResourceDto();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId,
                gson.toJson(eventData));

        PostRequestResourceDto resource = createPostRequestResourceDto();
        //Resource resourceInfo = createResource();

        //doReturn(0).when(contextEventService, "calculateScore", resourceInfo, resource.getAnswer());

        //PostRequestResourceDto result = WhiteboxImpl.invokeMethod(contextEventService, "updateExistingResourceDto",
        //        contextProfileEvent, resourceInfo, resource);

        //verifyPrivate(contextEventService, times(1)).invoke("calculateScore", resourceInfo, resource.getAnswer());

        //assertEquals("Wrong time spent", 2000, result.getTimeSpent());
        //assertEquals("Wrong resourceId", resourceId, result.getResourceId());
        //assertEquals("Wrong score", 0, result.getScore());
        //assertEquals("Wrong reaction", 5, result.getReaction());
    }

    @Ignore
    @Test
    public void findResourceInContext() throws Exception {
        //Resource resource = createResource();

        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);

        //Resource result = WhiteboxImpl.invokeMethod(contextEventService, "findResourceInContext",
        //        collectionResources, resourceId, contextId);

        //assertEquals("Wrong resource ID", resourceId, result.getId());
    }

    @Ignore
    @Test(expected = ContentNotFoundException.class)
    public void findResourceInContextThrowException() throws Exception {
        //Resource resource = createResource();

        //List<Resource> collectionResources =  new ArrayList<>();
        //collectionResources.add(resource);

        //WhiteboxImpl.invokeMethod(contextEventService, "findResourceInContext",
        //        collectionResources, previousResourceId, contextId);
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

    @Ignore
    @Test
    public void sendOnResourceEventMessage() throws Exception {
        ContextProfile contextProfile = createContextProfile();
        PostRequestResourceDto postRequestResourceDto = createPostRequestResourceDto();

        WhiteboxImpl.invokeMethod(contextEventService, "sendOnResourceEventMessage",
                contextProfile, postRequestResourceDto, new EventSummaryDataDto());
        verify(activeMQClientService, times(1))
                .sendOnResourceEventMessage(eq(contextId), eq(profileId), any(OnResourceEventMessageDto.class));
    }

    @Ignore
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
        verifyPrivate(contextEventService, times(0)).invoke("createSkippedContextProfileEvents", any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("doFinishContextEventTransaction", any(), any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("sendFinishContextEventMessage", any(), any(), any());
    }

    @Ignore
    @Test
    public void processFinishContextEvent() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);
        when(contextService.findById(contextId)).thenReturn(context);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        //doReturn(new ArrayList<Resource>()).when(contextEventService, "createSkippedContextProfileEvents",
        //        eq(contextProfileId), any(ArrayList.class));
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

    @Ignore
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
        //when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        //verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult1 = result.getProfileEvents().get(0);
        assertEquals("Wrong event size for assigneeId1", 1, profileResult1.getEvents().size());
        assertEquals("Wrong profile ID for assigneeId1", assigneeId1, profileResult1.getProfileId());
        assertEquals("Wrong current content id", resourceId, profileResult1.getCurrentResourceId());

        PostResponseResourceDto eventResult = profileResult1.getEvents().get(0);
        assertEquals("Wrong reaction", 3, eventResult.getReaction());
        assertEquals("Wrong timeSpent", 1000, eventResult.getTimeSpent());
        assertEquals("Wrong resource Id", bodyResourceId, eventResult.getResourceId());
        assertEquals("Wrong score", 100, eventResult.getScore());
        assertFalse("Wrong isSkipped value", eventResult.getIsSkipped());
        assertEquals("Wrong answer value", answerValue, eventResult.getAnswer().get(0).getValue());
    }

    @Ignore
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
        //when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        //verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

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
        //postRequestResourceDto.setResourceId(resourceId);
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

    private ContextProfileWithContextEntity createContextProfileContextEntity() {
        ContextProfileWithContextEntity entity = mock(ContextProfileWithContextEntity.class);

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