package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.CollectionMetadataDto;
import com.quizzes.api.core.dtos.ContextProfileDataDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.OnResourceEventResponseDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.TaxonomySummaryDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.enums.CollectionSetting;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.enums.settings.ShowFeedbackOptions;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.exceptions.NoAttemptsLeftException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.services.content.AnalyticsContentService;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import com.quizzes.api.util.QuizzesUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
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
    private CurrentContextProfileService currentContextProfileService;

    @Mock
    private ActiveMQClientService activeMQClientService;

    @Mock
    private CollectionService collectionService;

    @Mock
    private AnalyticsContentService analyticsContentService;

    @Mock
    private ClassMemberService classMemberService;

    @Mock
    private QuizzesUtils quizzesUtils;

    @Mock
    private Gson gson = new Gson();

    private UUID collectionId;
    private String token;
    private UUID contextId;
    private UUID classId;
    private UUID resourceId;
    private UUID previousResourceId;
    private UUID contextProfileId;
    private UUID eventId;
    private UUID profileId;
    private CurrentContextProfile currentContextProfile;
    private Timestamp startDate;

    @Before
    public void beforeEachTest() {
        collectionId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        contextId = UUID.randomUUID();
        classId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        previousResourceId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentContextProfile = new CurrentContextProfile();
        startDate = new Timestamp(System.currentTimeMillis());
    }

    @Test(expected = InvalidAssigneeException.class)
    public void processStartContextEventUsingInvalidClassMember() throws Exception {
        Context context = createContext();
        when(contextService.findById(contextId)).thenReturn(context);
        when(classMemberService.containsMemberId(any(), any(), any())).thenThrow(InvalidAssigneeException.class);

        contextEventService.processStartContextEvent(contextId, profileId, token);

        verify(contextService, times(1)).findById(contextId);
        verify(classMemberService, times(1)).containsMemberId(context.getClassId(), profileId, token);
        verify(currentContextProfileService, times(0)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(0)).findById(currentContextProfile.getContextProfileId());

        verifyPrivate(contextEventService, times(0)).invoke("createContextProfile", context, profileId, token);
        verifyPrivate(contextEventService, times(0)).invoke("resumeStartContextEvent", any(), any());
    }

    @Test
    public void processStartContextEventWithCompletedContextProfile() throws Exception {
        Context context = createContext();

        when(contextService.findById(contextId)).thenReturn(context);
        when(classMemberService.containsMemberId(context.getClassId(), profileId, token)).thenReturn(true);

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(true);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);

        StartContextEventResponseDto response = createStartContextEventResponseDto();
        doReturn(response).when(contextEventService, "createContextProfile", context, profileId, token);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId, token);

        verify(contextService, times(1)).findById(contextId);
        verify(classMemberService, times(1)).containsMemberId(context.getClassId(), profileId, token);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(currentContextProfile.getContextProfileId());

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfile", context, profileId, token);
        verifyPrivate(contextEventService, times(0)).invoke("resumeStartContextEvent", any(), any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContextEventWithIncompleteContextProfile() throws Exception {
        Context context = createContext();
        when(contextService.findById(contextId)).thenReturn(context);
        when(classMemberService.containsMemberId(context.getClassId(), profileId, token)).thenReturn(true);

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);

        StartContextEventResponseDto response = createStartContextEventResponseDto();
        doReturn(response).when(contextEventService, "resumeStartContextEvent", context, currentContextProfile);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId, token);

        verify(contextService, times(1)).findById(contextId);
        verify(classMemberService, times(1)).containsMemberId(context.getClassId(), profileId, token);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(currentContextProfile.getContextProfileId());

        verifyPrivate(contextEventService, times(0)).invoke("createContextProfile", context, profileId, token);
        verifyPrivate(contextEventService, times(1)).invoke("resumeStartContextEvent", any(), any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContextEventWithoutCurrentContextProfile() throws Exception {
        Context context = createContext();
        when(contextService.findById(contextId)).thenReturn(context);
        when(classMemberService.containsMemberId(context.getClassId(), profileId, token)).thenReturn(true);

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenThrow(ContentNotFoundException.class);

        ContextProfile contextProfile = createContextProfile();
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);

        StartContextEventResponseDto response = createStartContextEventResponseDto();
        doReturn(response).when(contextEventService, "createContextProfile", context, profileId, token);

        StartContextEventResponseDto result = contextEventService.processStartContextEvent(contextId, profileId, token);

        verify(contextService, times(1)).findById(contextId);
        verify(classMemberService, times(1)).containsMemberId(context.getClassId(), profileId, token);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(0)).findById(currentContextProfile.getContextProfileId());

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfile", context, profileId, token);
        verifyPrivate(contextEventService, times(0)).invoke("resumeStartContextEvent", any(), any());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void createContextProfilePrivateMethod() throws Exception {
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();

        doReturn(contextProfile).when(contextEventService, "createContextProfileObject", any(UUID.class),
                any(UUID.class));
        doReturn(contextProfile).when(contextEventService, "doCreateContextProfileTransaction", contextProfile);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "processStartContext",
                any(), any(), any(), any(ArrayList.class), eq(token), anyLong());

        Map<String, Object> setting = new HashMap<>();
        setting.put(CollectionSetting.AttemptsAllowed.getLiteral(), new Double(10));
        CollectionDto collectionDto = createCollectionDto(setting);
        when(collectionService.getCollectionOrAssessment(any(), anyBoolean())).thenReturn(collectionDto);

        StartContextEventResponseDto result = WhiteboxImpl.invokeMethod(contextEventService, "createContextProfile",
                context, profileId, token);

        verifyPrivate(contextEventService, times(1)).invoke("validateAttemptsLeft",
                context, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileObject", any(UUID.class),
                any(UUID.class));
        verifyPrivate(contextEventService, times(1)).invoke("doCreateContextProfileTransaction", contextProfile);
        verifyPrivate(contextEventService, times(1)).invoke("processStartContext", eq(context), eq(profileId),
                eq(contextProfile), any(ArrayList.class), eq(token), anyLong());

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void processStartContext() throws Exception {
        Context context = createContext();
        CollectionDto collection = createCollectionDto(new HashMap<>());
        collection.setId(collectionId.toString());
        collection.setIsCollection(true);
        collection.setResources(Arrays.asList(createResourceDto()));
        ContextProfile contextProfile = createContextProfile();

        when(collectionService.getCollectionOrAssessment(any(UUID.class), anyBoolean())).thenReturn(collection);
        doNothing().when(contextEventService, "sendStartEventMessage",  contextId, profileId, resourceId, true);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                eq(contextId), eq(resourceId), eq(collectionId), any(ArrayList.class));

        StartContextEventResponseDto result =  WhiteboxImpl.invokeMethod(contextEventService, "processStartContext",
                context, profileId, contextProfile, new ArrayList<>(), token, startDate.getTime());

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean());
        verify(analyticsContentService, times(1)).collectionPlayStart(any(UUID.class), any(UUID.class), any(UUID.class),
                any(UUID.class), anyBoolean(), anyString(), anyLong());

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
    public void processStartContextForAnonymousOrPreview() throws Exception {
        Context context = createContext();
        context.setClassId(null);

        CollectionDto collection = createCollectionDto(new HashMap<>());
        collection.setId(collectionId.toString());
        collection.setIsCollection(true);
        collection.setResources(Arrays.asList(createResourceDto()));
        ContextProfile contextProfile = createContextProfile();

        when(collectionService.getCollectionOrAssessment(any(UUID.class), anyBoolean())).thenReturn(collection);
        doNothing().when(contextEventService, "sendStartEventMessage",  contextId, profileId, resourceId, true);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                eq(contextId), eq(resourceId), eq(collectionId), any(ArrayList.class));

        StartContextEventResponseDto result =  WhiteboxImpl.invokeMethod(contextEventService, "processStartContext",
                context, profileId, contextProfile, new ArrayList<>(), token, startDate.getTime());

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean());
        verify(analyticsContentService, times(0)).collectionPlayStart(any(UUID.class), any(UUID.class), any(UUID.class),
                any(UUID.class), anyBoolean(), anyString(), anyLong());

        verifyPrivate(contextEventService, times(1)).invoke("prepareStartContextEventResponse", eq(contextId),
                eq(resourceId), eq(collectionId), any(ArrayList.class));
        verifyPrivate(contextEventService, times(0)).invoke("sendStartEventMessage",
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
        Context context = createContext();
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        CollectionDto collection = createCollectionDto(new HashMap<>());
        collection.setResources(Arrays.asList(createResourceDto()));

        when(collectionService.getCollectionOrAssessment(any(UUID.class), anyBoolean())).thenReturn(collection);
        doNothing().when(contextEventService, "sendStartEventMessage", any(UUID.class), any(UUID.class),
                any(UUID.class), anyBoolean());
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                any(UUID.class), any(UUID.class), any(UUID.class), anyList());

        StartContextEventResponseDto result = WhiteboxImpl.invokeMethod(contextEventService, "resumeStartContextEvent",
                context, currentContextProfile);

        verifyPrivate(contextEventService, times(1)).invoke("sendStartEventMessage", any(UUID.class), any(UUID.class),
                any(UUID.class), eq(false));
        verifyPrivate(contextEventService, times(1)).invoke("prepareStartContextEventResponse", any(UUID.class),
                any(UUID.class), any(UUID.class), anyList());
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test
    public void resumeStartContextEventForAnonymousOrPreview() throws Exception {
        Context context = createContext();
        context.setClassId(null);
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        CollectionDto collection = createCollectionDto(new HashMap<>());
        collection.setResources(Arrays.asList(createResourceDto()));

        when(collectionService.getCollectionOrAssessment(any(UUID.class), anyBoolean())).thenReturn(collection);
        doReturn(createStartContextEventResponseDto()).when(contextEventService, "prepareStartContextEventResponse",
                any(UUID.class), any(UUID.class), any(UUID.class), anyList());

        StartContextEventResponseDto result = WhiteboxImpl.invokeMethod(contextEventService, "resumeStartContextEvent",
                context, currentContextProfile);

        verifyPrivate(contextEventService, times(0)).invoke("sendStartEventMessage", any(UUID.class), any(UUID.class),
                any(UUID.class), eq(false));
        verifyPrivate(contextEventService, times(1)).invoke("prepareStartContextEventResponse", any(UUID.class),
                any(UUID.class), any(UUID.class), anyList());
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertNull("CurrentResource is not null", result.getCurrentResourceId());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertEquals("Wrong number of events", 0, result.getEvents().size());
    }

    @Test(expected = InvalidRequestException.class)
    public void processOnResourceEventWithoutCurrentEvent() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body, token);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
    }

    @Test
    public void processOnResourceEventWithEventWithoutAnswer() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());
        CollectionDto collectionDto = createCollectionDto(setting);
        when(collectionService.getCollectionOrAssessment(any())).thenReturn(collectionDto);
        when(currentContextProfile.getCurrentContextProfileId()).thenReturn(UUID.randomUUID());
        when(currentContextProfile.getIsComplete()).thenReturn(false);
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();
        TaxonomySummaryDto taxonomySummaryDto = new TaxonomySummaryDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        body.getPreviousResource().getAnswer().remove(0);

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        collectionDto.setResources(Arrays.asList(resource, previousResource));

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileEventService.save(any(ContextProfileEvent.class))).thenReturn(contextProfileEvent);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionDto).when(collectionService, "getCollectionOrAssessment", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(contextProfileEvent).when(contextEventService, "createContextProfileEvent",
                contextProfileId, previousResourceId);

        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", any(UUID.class), any(UUID.class),
                any(String.class), any(String.class), any(UUID.class));
        ContextProfile updatedContextProfile = new ContextProfile();
        updatedContextProfile.setUpdatedAt(new Timestamp(Instant.now().toEpochMilli()));
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(updatedContextProfile);
        when(contextProfileService.findById(contextProfileId)).thenReturn(contextProfile);

        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
        doNothing().when(contextEventService, "sendAnalyticsEvent", any(ContextEntity.class), any(UUID.class),
                anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body, token);

        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).save(any(ContextProfileEvent.class));
        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionDto.getResources(),
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionDto.getResources(),
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(1)).invoke("createContextProfileEvent", contextProfileId,
                previousResourceId);
        verifyPrivate(contextEventService, times(1)).invoke("calculateScore", previousResource,
                body.getPreviousResource().getAnswer());

        verifyPrivate(contextEventService, times(0)).invoke("updateExistingResourceDto", any(), any(), any());
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", any(UUID.class), any(UUID.class),
                any(String.class), any(String.class), any(UUID.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
        verifyPrivate(contextEventService, times(1)).invoke("sendAnalyticsEvent", any(ContextEntity.class),
                any(UUID.class), anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));
    }

    @Test
    public void processOnResourceEventExistingEvent() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());
        CollectionDto collectionDto = createCollectionDto(setting);
        when(collectionService.getCollectionOrAssessment(any())).thenReturn(collectionDto);
        when(currentContextProfile.getCurrentContextProfileId()).thenReturn(UUID.randomUUID());
        when(currentContextProfile.getIsComplete()).thenReturn(false);
        when(currentContextProfile.getIsCollection()).thenReturn(true);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();
        TaxonomySummaryDto taxonomySummaryDto = new TaxonomySummaryDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = Arrays.asList(contextProfileEvent);
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);
        collectionDto.setResources(collectionResources);

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionDto).when(collectionService, "getCollectionOrAssessment", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        when(contextProfileEventService.save(any(ContextProfileEvent.class))).thenReturn(contextProfileEvent);

        doReturn(body.getPreviousResource()).when(contextEventService, "updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        List<TaxonomySummaryDto> taxonomySummaryDtoList = new ArrayList<>();
        taxonomySummaryDtoList.add(taxonomySummaryDto);
        doReturn(taxonomySummaryDtoList).when(contextEventService, "calculateTaxonomySummary", contextProfileEvents, false, collectionDto, eventSummaryDataDto);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", any(UUID.class), any(UUID.class),
                any(String.class), any(String.class), any(UUID.class));
        ContextProfile updatedContextProfile = new ContextProfile();
        updatedContextProfile.setUpdatedAt(new Timestamp(Instant.now().toEpochMilli()));
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(updatedContextProfile);
        doNothing().when(contextEventService, "sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
        doNothing().when(contextEventService, "sendAnalyticsEvent", any(ContextEntity.class), any(UUID.class),
                anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body, token);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verify(contextProfileEventService, times(1)).save(any(ContextProfileEvent.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));

        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(0)).invoke("createContextProfileEvent", any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", any(), any());

        verifyPrivate(contextEventService, times(1)).invoke("updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", any(UUID.class), any(UUID.class),
                any(String.class), any(String.class), any(UUID.class));
        verifyPrivate(contextEventService, times(1)).invoke("sendOnResourceEventMessage",
                contextProfile, body.getPreviousResource(), eventSummaryDataDto);
        verifyPrivate(contextEventService, times(1)).invoke("sendAnalyticsEvent", any(ContextEntity.class), any(UUID.class),
                anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));
    }

    @Test
    public void processOnResourceEventExistingEventForAnonymousOrPreview() throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());
        CollectionDto collectionDto = createCollectionDto(setting);
        when(collectionService.getCollectionOrAssessment(any())).thenReturn(collectionDto);

        when(currentContextProfile.getCurrentContextProfileId()).thenReturn(UUID.randomUUID());
        when(currentContextProfile.getIsComplete()).thenReturn(false);
        when(currentContextProfile.getIsCollection()).thenReturn(true);
        when(currentContextProfile.getClassId()).thenReturn(null);

        ResourceDto resource = createResourceDto();
        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();
        TaxonomySummaryDto taxonomySummaryDto = new TaxonomySummaryDto();

        ContextProfile contextProfile = createContextProfile();
        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = Arrays.asList(contextProfileEvent);
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);
        collectionDto.setResources(collectionResources);

        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileEventService.save(any(ContextProfileEvent.class))).thenReturn(contextProfileEvent);

        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        doReturn(collectionDto).when(collectionService, "getCollectionOrAssessment", collectionId, true);
        doReturn(resource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                previousResourceId, contextId);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);

        doReturn(body.getPreviousResource()).when(contextEventService, "updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        List<TaxonomySummaryDto> taxonomySummaryDtoList = new ArrayList<>();
        taxonomySummaryDtoList.add(taxonomySummaryDto);
        doReturn(taxonomySummaryDtoList).when(contextEventService, "calculateTaxonomySummary", contextProfileEvents, false, collectionDto, eventSummaryDataDto);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", any(UUID.class), any(UUID.class),
                anyString(), anyString(), any(UUID.class));
        doNothing().when(contextEventService, "sendOnResourceEventMessage", any(), any(), any());

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body, token);

        verify(currentContextProfileService, times(1)).findCurrentContextProfileByContextIdAndProfileId(
                contextId, profileId);
        verify(contextProfileEventService, times(1)).save(any(ContextProfileEvent.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));

        verifyPrivate(contextEventService, times(1)).invoke("getPreviousResource", body);
        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                resourceId, contextId);
        verifyPrivate(contextEventService, times(1)).invoke("findResourceInContext", collectionResources,
                previousResourceId, contextId);

        verifyPrivate(contextEventService, times(0)).invoke("createContextProfileEvent", any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("calculateScore", any(), any());

        verifyPrivate(contextEventService, times(1)).invoke("updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, false);
        verifyPrivate(contextEventService, times(1)).invoke("updateContextProfile", any(UUID.class), any(UUID.class),
                anyString(), anyString(), any(UUID.class));
        verifyPrivate(contextEventService, times(0)).invoke("sendOnResourceEventMessage", any(), any(), any());
        verifyPrivate(contextEventService, times(0)).invoke("sendAnalyticsEvent", any(ContextEntity.class), any(UUID.class),
                anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));
    }

    @Test
    public void processOnResourceEventWithFeedback() throws Exception {

        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());

        OnResourceEventPostRequestDto body = processOnResourceEventWithSetting(setting);
        OnResourceEventResponseDto response = contextEventService.processOnResourceEvent(contextId, profileId,
                resourceId, body, token);
        assertNotNull(response);
        assertNotNull(response.getScore());
    }

    @Test
    public void processOnResourceEventWithNoFeedback() throws Exception {

        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Never.getLiteral());

        OnResourceEventPostRequestDto body = processOnResourceEventWithSetting(setting);
        OnResourceEventResponseDto response = contextEventService.processOnResourceEvent(contextId, profileId,
                resourceId, body, token);
        assertNotNull(response);
        assertNull(response.getScore());
    }

    @Test
    public void processOnResourceEventFeedbackWithNoSettings() throws Exception {

        OnResourceEventPostRequestDto body = processOnResourceEventWithSetting(null);
        OnResourceEventResponseDto response = contextEventService.processOnResourceEvent(contextId, profileId,
                resourceId, body, token);
        assertNotNull(response);
        assertNull(response.getScore());
    }

    private OnResourceEventPostRequestDto processOnResourceEventWithSetting(Map<String, Object> setting) throws Exception {
        ContextProfileEntity currentContextProfile = createContextProfileEntity();
        when(currentContextProfile.getCurrentContextProfileId()).thenReturn(UUID.randomUUID());
        when(currentContextProfile.getIsComplete()).thenReturn(false);
        when(currentContextProfile.getIsCollection()).thenReturn(true);
        when(currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        ResourceDto previousResource = createResourceDto();
        previousResource.setId(previousResourceId);
        ResourceDto resource = createResourceDto();

        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();
        TaxonomySummaryDto taxonomySummaryDto = new TaxonomySummaryDto();
        CollectionDto collectionDto = createCollectionDto(setting);
        List<ResourceDto> collectionResources = Arrays.asList(resource, previousResource);
        collectionDto.setResources(collectionResources);

        ContextProfile contextProfile = createContextProfile();
        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, previousResourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = Arrays.asList(contextProfileEvent);

        List<AnswerDto> answers = new ArrayList<>();
        answers.add(createAnswerDto("A"));

        OnResourceEventPostRequestDto body = createOnResourceEventPostRequestDto();
        ResourceMetadataDto resourceMetadataDto = createQuestionDataDto(answers, QuestionTypeEnum.TrueFalse.getLiteral());
        previousResource.setMetadata(resourceMetadataDto);

        when(collectionService.getCollectionOrAssessment(collectionId, true)).thenReturn(collectionDto);

        doReturn(resource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                resourceId, contextId);
        doReturn(previousResource).when(contextEventService, "findResourceInContext", collectionDto.getResources(),
                previousResourceId, contextId);
        doReturn(body.getPreviousResource()).when(contextEventService, "getPreviousResource", body);
        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        when(contextProfileEventService.save(any(ContextProfileEvent.class))).thenReturn(contextProfileEvent);

        doReturn(body.getPreviousResource()).when(contextEventService, "updateExistingResourceDto",
                eq(contextProfileEvent), eq(previousResource), any(PostRequestResourceDto.class));
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, false);
        List<TaxonomySummaryDto> taxonomySummaryDtoList = new ArrayList<>();
        taxonomySummaryDtoList.add(taxonomySummaryDto);
        doReturn(taxonomySummaryDtoList).when(contextEventService, "calculateTaxonomySummary", contextProfileEvents, false, collectionDto, eventSummaryDataDto);
        doReturn(contextProfile).when(contextEventService, "updateContextProfile", any(UUID.class), any(UUID.class),
                any(String.class), any(String.class), any(UUID.class));
        ContextProfile updatedContextProfile = new ContextProfile();
        updatedContextProfile.setUpdatedAt(new Timestamp(Instant.now().toEpochMilli()));
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(updatedContextProfile);
        doNothing().when(contextEventService, "sendOnResourceEventMessage", any(), any(), any());
        doNothing().when(contextEventService,"sendAnalyticsEvent", any(ContextEntity.class), any(UUID.class),
                anyString(), any(ResourceDto.class), any(PostRequestResourceDto.class), any(UUID.class));

        return body;
    }

    @Test
    public void calculateCollectionTaxonomy() throws Exception {
        CollectionMetadataDto collectionMetadataDto = new CollectionMetadataDto();

        Map<String, Object> taxonomyMap = new HashMap<>();
        taxonomyMap.put("Taxonomy01", new String());
        taxonomyMap.put("Taxonomy02", new String());
        taxonomyMap.put("Taxonomy03", new String());
        collectionMetadataDto.setTaxonomy(taxonomyMap);

        CollectionDto collectionDto =  new CollectionDto();
        collectionDto.setId(UUID.randomUUID().toString());
        collectionDto.setMetadata(collectionMetadataDto);

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(UUID.randomUUID());
        contextProfileEvents.add(contextProfileEvent1);
        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(UUID.randomUUID());
        contextProfileEvents.add(contextProfileEvent2);

        EventSummaryDataDto eventSummary = new EventSummaryDataDto();
        eventSummary.setAverageScore((short)50);
        eventSummary.setTotalCorrect((short)1);
        eventSummary.setAverageReaction((short)1);
        eventSummary.setTotalTimeSpent(3000);
        eventSummary.setTotalAnswered((short)2);

        Map<String, TaxonomySummaryDto> result = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateCollectionTaxonomy", collectionDto, contextProfileEvents, eventSummary);

        assertNotNull("Calculation result is null", result);
        assertEquals("Collection Taxonomy Summary is wrong", 3, result.size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy01"));
        assertEquals("All the Taxonomies summaries must contain 2 Resources", 2,
                result.get("Taxonomy01").getResources().size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy02"));
        assertEquals("All the Taxonomies summaries must contain 2 Resources", 2,
                result.get("Taxonomy02").getResources().size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy03"));
        assertEquals("All the Taxonomies summaries must contain 2 Resources", 2,
                result.get("Taxonomy03").getResources().size());
    }

    @Test
    public void calculateCollectionTaxonomyWithNoCollectionTaxonomy() throws Exception {
        CollectionMetadataDto collectionMetadataDto = new CollectionMetadataDto();

        CollectionDto collectionDto =  new CollectionDto();
        collectionDto.setId(UUID.randomUUID().toString());
        collectionDto.setMetadata(collectionMetadataDto);

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(UUID.randomUUID());
        contextProfileEvents.add(contextProfileEvent1);
        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(UUID.randomUUID());
        contextProfileEvents.add(contextProfileEvent2);

        EventSummaryDataDto eventSummary = new EventSummaryDataDto();
        eventSummary.setAverageScore((short)50);
        eventSummary.setTotalCorrect((short)1);
        eventSummary.setAverageReaction((short)1);
        eventSummary.setTotalTimeSpent(3000);
        eventSummary.setTotalAnswered((short)2);

        Map<String, TaxonomySummaryDto> result = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateCollectionTaxonomy", collectionDto, contextProfileEvents, eventSummary);

        assertNotNull("Calculation result is null", result);
        assertTrue("Collection Taxonomy should be empty", result.isEmpty());
    }

    @Test
    public void calculateCollectionTaxonomyWithNoEvents() throws Exception {
        CollectionMetadataDto collectionMetadataDto = new CollectionMetadataDto();

        Map<String, Object> taxonomyMap = new HashMap<>();
        taxonomyMap.put("Taxonomy01", new String());
        taxonomyMap.put("Taxonomy02", new String());
        taxonomyMap.put("Taxonomy03", new String());
        collectionMetadataDto.setTaxonomy(taxonomyMap);

        CollectionDto collectionDto =  new CollectionDto();
        collectionDto.setId(UUID.randomUUID().toString());
        collectionDto.setMetadata(collectionMetadataDto);

        EventSummaryDataDto eventSummary = new EventSummaryDataDto();
        eventSummary.setAverageScore((short)0);
        eventSummary.setTotalCorrect((short)0);
        eventSummary.setAverageReaction((short)0);
        eventSummary.setTotalTimeSpent(0);
        eventSummary.setTotalAnswered((short)0);

        Map<String, TaxonomySummaryDto> result = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateCollectionTaxonomy", collectionDto, Collections.emptyList(), eventSummary);

        assertNotNull("Calculation result is null", result);
        assertEquals("Collection Taxonomy Summary is wrong", 3, result.size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy01"));
        assertEquals("All the Taxonomies summaries must contain 0 Resources", 0,
                result.get("Taxonomy01").getResources().size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy02"));
        assertEquals("All the Taxonomies summaries must contain 0 Resources", 0,
                result.get("Taxonomy02").getResources().size());
        assertNotNull("The Taxonomy must have a summary", result.get("Taxonomy03"));
        assertEquals("All the Taxonomies summaries must contain 0 Resources", 0,
                result.get("Taxonomy03").getResources().size());
    }

    @Test
    public void getEventsByTaxonomy() throws Exception {
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        UUID resourceId3 = UUID.randomUUID();
        UUID resourceId4 = UUID.randomUUID();
        UUID resourceId5 = UUID.randomUUID();

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(resourceId1);
        contextProfileEvents.add(contextProfileEvent1);
        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(resourceId2);
        contextProfileEvents.add(contextProfileEvent2);
        ContextProfileEvent contextProfileEvent3 = new ContextProfileEvent();
        contextProfileEvent3.setId(UUID.randomUUID());
        contextProfileEvent3.setResourceId(resourceId3);
        contextProfileEvents.add(contextProfileEvent3);
        ContextProfileEvent contextProfileEvent4 = new ContextProfileEvent();
        contextProfileEvent4.setId(UUID.randomUUID());
        contextProfileEvent4.setResourceId(resourceId4);
        contextProfileEvents.add(contextProfileEvent4);
        ContextProfileEvent contextProfileEvent5 = new ContextProfileEvent();
        contextProfileEvent5.setId(UUID.randomUUID());
        contextProfileEvent5.setResourceId(resourceId5);
        contextProfileEvents.add(contextProfileEvent5);

        List<ResourceDto> collectionResources = new ArrayList<>();
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setId(resourceId1);
        ResourceMetadataDto resourceMetadataDto1 = new ResourceMetadataDto();
        Map<String, Object> taxonomy1 = new HashMap<>();
        taxonomy1.put("Taxonomy02", new String());
        taxonomy1.put("Taxonomy04", new String());
        resourceMetadataDto1.setTaxonomy(taxonomy1);
        resourceDto1.setMetadata(resourceMetadataDto1);
        collectionResources.add(resourceDto1);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setId(resourceId2);
        ResourceMetadataDto resourceMetadataDto2 = new ResourceMetadataDto();
        Map<String, Object> taxonomy2 = new HashMap<>();
        taxonomy2.put("Taxonomy05", new String());
        taxonomy2.put("Taxonomy06", new String());
        resourceMetadataDto2.setTaxonomy(taxonomy2);
        resourceDto2.setMetadata(resourceMetadataDto2);
        collectionResources.add(resourceDto2);

        ResourceDto resourceDto3 = new ResourceDto();
        resourceDto3.setId(resourceId3);
        ResourceMetadataDto resourceMetadataDto3 = new ResourceMetadataDto();
        Map<String, Object> taxonomy3 = new HashMap<>();
        taxonomy3.put("Taxonomy01", new String());
        taxonomy3.put("Taxonomy06", new String());
        resourceMetadataDto3.setTaxonomy(taxonomy3);
        resourceDto3.setMetadata(resourceMetadataDto3);
        collectionResources.add(resourceDto3);

        // A Resource without Taxonomy
        ResourceDto resourceDto4 = new ResourceDto();
        resourceDto4.setId(resourceId4);
        ResourceMetadataDto resourceMetadataDto4 = new ResourceMetadataDto();
        resourceDto4.setMetadata(resourceMetadataDto4);
        collectionResources.add(resourceDto4);

        ResourceDto resourceDto5 = new ResourceDto();
        resourceDto5.setId(resourceId5);
        ResourceMetadataDto resourceMetadataDto5 = new ResourceMetadataDto();
        Map<String, Object> taxonomy5 = new HashMap<>();
        taxonomy5.put("Taxonomy04", new String());
        taxonomy5.put("Taxonomy05", new String());
        taxonomy5.put("Taxonomy06", new String());
        resourceMetadataDto5.setTaxonomy(taxonomy5);
        resourceDto5.setMetadata(resourceMetadataDto5);
        collectionResources.add(resourceDto5);

        Set<String> collectionTaxonomyIds = new HashSet<>();
        collectionTaxonomyIds.add("Taxonomy01");
        collectionTaxonomyIds.add("Taxonomy02");
        collectionTaxonomyIds.add("Taxonomy03");

        Map<String, List<ContextProfileEvent>> result = WhiteboxImpl.invokeMethod(contextEventService,
                "getEventsByTaxonomy", contextProfileEvents, collectionResources, collectionTaxonomyIds);

        assertNotNull("Result is null", result);
        assertEquals("Wrong Taxonomies number", 3, result.size());
        assertNull("Collection Taxonomy should be excluded", result.get("Taxonomy01"));
        assertNull("Collection Taxonomy should be excluded", result.get("Taxonomy02"));
        assertNull("Collection Taxonomy should be excluded", result.get("Taxonomy03"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy04"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy05"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy06"));

    }

    @Test
    public void getEventsByTaxonomyWithNoAdditionalTaxonomies() throws Exception {
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        UUID resourceId3 = UUID.randomUUID();
        UUID resourceId4 = UUID.randomUUID();
        UUID resourceId5 = UUID.randomUUID();

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(resourceId1);
        contextProfileEvents.add(contextProfileEvent1);
        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(resourceId2);
        contextProfileEvents.add(contextProfileEvent2);
        ContextProfileEvent contextProfileEvent3 = new ContextProfileEvent();
        contextProfileEvent3.setId(UUID.randomUUID());
        contextProfileEvent3.setResourceId(resourceId3);
        contextProfileEvents.add(contextProfileEvent3);
        ContextProfileEvent contextProfileEvent4 = new ContextProfileEvent();
        contextProfileEvent4.setId(UUID.randomUUID());
        contextProfileEvent4.setResourceId(resourceId4);
        contextProfileEvents.add(contextProfileEvent4);
        ContextProfileEvent contextProfileEvent5 = new ContextProfileEvent();
        contextProfileEvent5.setId(UUID.randomUUID());
        contextProfileEvent5.setResourceId(resourceId5);
        contextProfileEvents.add(contextProfileEvent5);

        List<ResourceDto> collectionResources = new ArrayList<>();
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setId(resourceId1);
        ResourceMetadataDto resourceMetadataDto1 = new ResourceMetadataDto();
        Map<String, Object> taxonomy1 = new HashMap<>();
        taxonomy1.put("Taxonomy02", new String());
        resourceMetadataDto1.setTaxonomy(taxonomy1);
        resourceDto1.setMetadata(resourceMetadataDto1);
        collectionResources.add(resourceDto1);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setId(resourceId2);
        ResourceMetadataDto resourceMetadataDto2 = new ResourceMetadataDto();
        Map<String, Object> taxonomy2 = new HashMap<>();

        resourceDto2.setMetadata(resourceMetadataDto2);
        collectionResources.add(resourceDto2);

        ResourceDto resourceDto3 = new ResourceDto();
        resourceDto3.setId(resourceId3);
        ResourceMetadataDto resourceMetadataDto3 = new ResourceMetadataDto();
        Map<String, Object> taxonomy3 = new HashMap<>();
        taxonomy3.put("Taxonomy01", new String());
        resourceMetadataDto3.setTaxonomy(taxonomy3);
        resourceDto3.setMetadata(resourceMetadataDto3);
        collectionResources.add(resourceDto3);

        // A Resource without Taxonomy
        ResourceDto resourceDto4 = new ResourceDto();
        resourceDto4.setId(resourceId4);
        ResourceMetadataDto resourceMetadataDto4 = new ResourceMetadataDto();
        resourceDto4.setMetadata(resourceMetadataDto4);
        collectionResources.add(resourceDto4);

        ResourceDto resourceDto5 = new ResourceDto();
        resourceDto5.setId(resourceId5);
        ResourceMetadataDto resourceMetadataDto5 = new ResourceMetadataDto();
        Map<String, Object> taxonomy5 = new HashMap<>();
        resourceDto5.setMetadata(resourceMetadataDto5);
        collectionResources.add(resourceDto5);

        Set<String> collectionTaxonomyIds = new HashSet<>();
        collectionTaxonomyIds.add("Taxonomy01");
        collectionTaxonomyIds.add("Taxonomy02");
        collectionTaxonomyIds.add("Taxonomy03");

        Map<String, List<ContextProfileEvent>> result = WhiteboxImpl.invokeMethod(contextEventService,
                "getEventsByTaxonomy", contextProfileEvents, collectionResources, collectionTaxonomyIds);

        assertNotNull("Result is null", result);
        assertTrue("Additional Taxonomies list should be empty", result.isEmpty());

    }

    @Test
    public void getEventsByTaxonomyWithNoCollectionTaxonomy() throws Exception {
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        UUID resourceId3 = UUID.randomUUID();
        UUID resourceId4 = UUID.randomUUID();
        UUID resourceId5 = UUID.randomUUID();

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(resourceId1);
        contextProfileEvents.add(contextProfileEvent1);
        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(resourceId2);
        contextProfileEvents.add(contextProfileEvent2);
        ContextProfileEvent contextProfileEvent3 = new ContextProfileEvent();
        contextProfileEvent3.setId(UUID.randomUUID());
        contextProfileEvent3.setResourceId(resourceId3);
        contextProfileEvents.add(contextProfileEvent3);
        ContextProfileEvent contextProfileEvent4 = new ContextProfileEvent();
        contextProfileEvent4.setId(UUID.randomUUID());
        contextProfileEvent4.setResourceId(resourceId4);
        contextProfileEvents.add(contextProfileEvent4);
        ContextProfileEvent contextProfileEvent5 = new ContextProfileEvent();
        contextProfileEvent5.setId(UUID.randomUUID());
        contextProfileEvent5.setResourceId(resourceId5);
        contextProfileEvents.add(contextProfileEvent5);

        List<ResourceDto> collectionResources = new ArrayList<>();
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setId(resourceId1);
        ResourceMetadataDto resourceMetadataDto1 = new ResourceMetadataDto();
        Map<String, Object> taxonomy1 = new HashMap<>();
        taxonomy1.put("Taxonomy02", new String());
        taxonomy1.put("Taxonomy04", new String());
        resourceMetadataDto1.setTaxonomy(taxonomy1);
        resourceDto1.setMetadata(resourceMetadataDto1);
        collectionResources.add(resourceDto1);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setId(resourceId2);
        ResourceMetadataDto resourceMetadataDto2 = new ResourceMetadataDto();
        Map<String, Object> taxonomy2 = new HashMap<>();
        taxonomy2.put("Taxonomy05", new String());
        taxonomy2.put("Taxonomy06", new String());
        resourceMetadataDto2.setTaxonomy(taxonomy2);
        resourceDto2.setMetadata(resourceMetadataDto2);
        collectionResources.add(resourceDto2);

        ResourceDto resourceDto3 = new ResourceDto();
        resourceDto3.setId(resourceId3);
        ResourceMetadataDto resourceMetadataDto3 = new ResourceMetadataDto();
        Map<String, Object> taxonomy3 = new HashMap<>();
        taxonomy3.put("Taxonomy01", new String());
        taxonomy3.put("Taxonomy06", new String());
        resourceMetadataDto3.setTaxonomy(taxonomy3);
        resourceDto3.setMetadata(resourceMetadataDto3);
        collectionResources.add(resourceDto3);

        // A Resource without Taxonomy
        ResourceDto resourceDto4 = new ResourceDto();
        resourceDto4.setId(resourceId4);
        ResourceMetadataDto resourceMetadataDto4 = new ResourceMetadataDto();
        resourceDto4.setMetadata(resourceMetadataDto4);
        collectionResources.add(resourceDto4);

        ResourceDto resourceDto5 = new ResourceDto();
        resourceDto5.setId(resourceId5);
        ResourceMetadataDto resourceMetadataDto5 = new ResourceMetadataDto();
        Map<String, Object> taxonomy5 = new HashMap<>();
        taxonomy5.put("Taxonomy04", new String());
        taxonomy5.put("Taxonomy05", new String());
        taxonomy5.put("Taxonomy06", new String());
        resourceMetadataDto5.setTaxonomy(taxonomy5);
        resourceDto5.setMetadata(resourceMetadataDto5);
        collectionResources.add(resourceDto5);

        Map<String, List<ContextProfileEvent>> result = WhiteboxImpl.invokeMethod(contextEventService,
                "getEventsByTaxonomy", contextProfileEvents, collectionResources, Collections.emptySet());

        assertNotNull("Result is null", result);
        assertEquals("Wrong Taxonomies number", 5, result.size());
        assertNotNull("Collection Taxonomy should be excluded", result.get("Taxonomy01"));
        assertNotNull("Collection Taxonomy should be excluded", result.get("Taxonomy02"));
        assertNull("Collection Taxonomy should be excluded", result.get("Taxonomy03"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy04"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy05"));
        assertNotNull("Event Taxonomy should be included", result.get("Taxonomy06"));

    }

    @Test
    public void mapEventsByTaxonomyToTaxonomySummaryList() throws Exception {
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        UUID resourceId3 = UUID.randomUUID();
        UUID resourceId4 = UUID.randomUUID();
        UUID resourceId5 = UUID.randomUUID();

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setResourceId(resourceId1);
        PostRequestResourceDto eventDataDto1 = new PostRequestResourceDto();
        eventDataDto1.setTimeSpent(1000);
        eventDataDto1.setReaction(1);
        eventDataDto1.setResourceId(resourceId1);
        eventDataDto1.setScore(100);
        eventDataDto1.setIsSkipped(false);
        contextProfileEvent1.setEventData(gson.toJson(eventDataDto1));
        contextProfileEvents.add(contextProfileEvent1);

        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setResourceId(resourceId2);
        PostRequestResourceDto eventDataDto2 = new PostRequestResourceDto();
        eventDataDto2.setTimeSpent(1000);
        eventDataDto2.setReaction(1);
        eventDataDto2.setResourceId(resourceId1);
        eventDataDto2.setScore(100);
        eventDataDto2.setIsSkipped(false);
        contextProfileEvent2.setEventData(gson.toJson(eventDataDto2));
        contextProfileEvents.add(contextProfileEvent2);

        ContextProfileEvent contextProfileEvent3 = new ContextProfileEvent();
        contextProfileEvent3.setId(UUID.randomUUID());
        PostRequestResourceDto eventDataDto3 = new PostRequestResourceDto();
        eventDataDto3.setTimeSpent(1000);
        eventDataDto3.setReaction(1);
        eventDataDto3.setResourceId(resourceId1);
        eventDataDto3.setScore(100);
        eventDataDto3.setIsSkipped(false);
        contextProfileEvent3.setEventData(gson.toJson(eventDataDto3));
        contextProfileEvent3.setResourceId(resourceId3);
        contextProfileEvents.add(contextProfileEvent3);

        ContextProfileEvent contextProfileEvent4 = new ContextProfileEvent();
        contextProfileEvent4.setId(UUID.randomUUID());
        contextProfileEvent4.setResourceId(resourceId4);
        PostRequestResourceDto eventDataDto4 = new PostRequestResourceDto();
        eventDataDto4.setTimeSpent(1000);
        eventDataDto4.setReaction(1);
        eventDataDto4.setResourceId(resourceId1);
        eventDataDto4.setScore(100);
        eventDataDto4.setIsSkipped(false);
        contextProfileEvent4.setEventData(gson.toJson(eventDataDto4));
        contextProfileEvents.add(contextProfileEvent4);

        ContextProfileEvent contextProfileEvent5 = new ContextProfileEvent();
        contextProfileEvent5.setId(UUID.randomUUID());
        contextProfileEvent5.setResourceId(resourceId5);
        PostRequestResourceDto eventDataDto5 = new PostRequestResourceDto();
        eventDataDto5.setTimeSpent(1000);
        eventDataDto5.setReaction(1);
        eventDataDto5.setResourceId(resourceId1);
        eventDataDto5.setScore(100);
        eventDataDto5.setIsSkipped(false);
        contextProfileEvent5.setEventData(gson.toJson(eventDataDto5));
        contextProfileEvents.add(contextProfileEvent5);

        Map<String, List<ContextProfileEvent>> eventsByTaxonomy = new HashMap<>();
        List<ContextProfileEvent> contextProfileEvents4 = new ArrayList<>();
        contextProfileEvents4.add(contextProfileEvent1);
        eventsByTaxonomy.put("Taxonomy04", contextProfileEvents4);

        List<ContextProfileEvent> contextProfileEvents5 = new ArrayList<>();
        contextProfileEvents5.add(contextProfileEvent2);
        contextProfileEvents5.add(contextProfileEvent5);
        eventsByTaxonomy.put("Taxonomy05", contextProfileEvents5);

        List<ContextProfileEvent> contextProfileEvents6 = new ArrayList<>();
        contextProfileEvents6.add(contextProfileEvent1);
        contextProfileEvents5.add(contextProfileEvent5);
        eventsByTaxonomy.put("Taxonomy06", contextProfileEvents6);

        List<TaxonomySummaryDto> result = WhiteboxImpl.invokeMethod(contextEventService,
                "mapEventsByTaxonomyToTaxonomySummaryList", eventsByTaxonomy, false);

        assertNotNull("Taxonomy Summary List is null", result);
        assertEquals("Taxonomy Summary List number of elements is wrong", 3, result.size());
    }

    @Test
    public void mapEventsByTaxonomyToTaxonomySummaryListWithNoEventsByTaxonomy() throws Exception {

        List<TaxonomySummaryDto> result = WhiteboxImpl.invokeMethod(contextEventService,
                "mapEventsByTaxonomyToTaxonomySummaryList", Collections.emptyMap(), false);

        assertNotNull("Taxonomy Summary List is null", result);
        assertTrue("Taxonomy Summary List should be empty", result.isEmpty());
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
        verifyPrivate(contextEventService, times(0)).invoke("finishContextEvent", any(), any(), any());
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
        doNothing().when(contextEventService, "finishContextEvent", context, contextProfile, token);

        contextEventService.processFinishContextEvent(contextId, profileId, token);

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(contextProfileService, times(1)).findById(contextProfileId);
        verify(contextService, times(1)).findById(contextId);
        verifyPrivate(contextEventService, times(1)).invoke("finishContextEvent", context, contextProfile, token);
    }

    @Test
    public void finishContextEvent() throws Exception {
        Context context = createContext();
        ContextProfile contextProfile = createContextProfile();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        List<ResourceDto> resources = new ArrayList<>();

        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());
        CollectionDto collectionDto = createCollectionDto(setting);
        collectionDto.setResources(resources);

        when(collectionService.getCollectionOrAssessment(collectionId, true)).thenReturn(collectionDto);
        doReturn(resources).when(contextEventService, "getResourcesToCreate", contextProfileEvents, resources);
        doReturn(contextProfileEvents).when(contextEventService, "createSkippedContextProfileEvents",
                contextProfileId, resources);
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, true);
        doNothing().when(contextEventService, "doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        doNothing().when(contextEventService, "sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);

        WhiteboxImpl.invokeMethod(contextEventService, "finishContextEvent", context, contextProfile, token);

        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
        verify(collectionService, times(1)).getCollectionOrAssessment(collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("getResourcesToCreate", contextProfileEvents, resources);
        verifyPrivate(contextEventService, times(1)).invoke("createSkippedContextProfileEvents",
                contextProfileId, resources);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, true);
        verifyPrivate(contextEventService, times(1)).invoke("doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        verifyPrivate(contextEventService, times(1)).invoke("sendFinishContextEventMessage",
                contextId, profileId, eventSummaryDataDto);
        verify(analyticsContentService, times(1)).collectionPlayStop(collectionId, classId, contextProfileId,
                profileId, true, token, startDate.getTime());
    }

    @Test
    public void finishContextEventForAnonymousOrPreview() throws Exception {
        Context context = createContext();
        context.setClassId(null);
        ContextProfile contextProfile = createContextProfile();

        ContextProfileEvent contextProfileEvent = createContextProfileEvent(contextProfileId, resourceId, "{}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent);
        EventSummaryDataDto eventSummaryDataDto = new EventSummaryDataDto();

        List<ResourceDto> resources = new ArrayList<>();

        when(contextProfileEventService.findByContextProfileId(contextProfileId)).thenReturn(contextProfileEvents);
        Map<String, Object> setting = new HashMap();
        setting.put(CollectionSetting.ShowFeedback.getLiteral(), ShowFeedbackOptions.Immediate.getLiteral());
        CollectionDto collectionDto = createCollectionDto(setting);
        collectionDto.setResources(resources);
        when(collectionService.getCollectionOrAssessment(collectionId, true)).thenReturn(collectionDto);
        doReturn(resources).when(contextEventService, "getResourcesToCreate", contextProfileEvents, resources);
        doReturn(contextProfileEvents).when(contextEventService, "createSkippedContextProfileEvents",
                contextProfileId, resources);
        doReturn(eventSummaryDataDto).when(contextEventService, "calculateEventSummary", contextProfileEvents, true);
        doNothing().when(contextEventService, "doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        doNothing().when(contextEventService, "sendFinishContextEventMessage", any(), any(), any());

        WhiteboxImpl.invokeMethod(contextEventService, "finishContextEvent", context, contextProfile, token);

        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
        verify(collectionService, times(1)).getCollectionOrAssessment(collectionId, true);
        verifyPrivate(contextEventService, times(1)).invoke("getResourcesToCreate", contextProfileEvents, resources);
        verifyPrivate(contextEventService, times(1)).invoke("createSkippedContextProfileEvents",
                contextProfileId, resources);
        verifyPrivate(contextEventService, times(1)).invoke("calculateEventSummary", contextProfileEvents, true);
        verifyPrivate(contextEventService, times(1)).invoke("doFinishContextEventTransaction",
                contextProfile, contextProfileEvents);
        verifyPrivate(contextEventService, times(0)).invoke("sendFinishContextEventMessage",
                any(), any(), any());
        verify(analyticsContentService, times(0)).collectionPlayStop(any(UUID.class), any(UUID.class), any(UUID.class),
                any(UUID.class), anyBoolean(), anyString(), any(long.class));
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

    @Test
    public void validateAttemptsValid() throws Exception {
        validateAttemptsPrivateMethod(2, 1);
    }

    @Test
    public void validateAttemptsNoSetting() throws Exception {
        validateAttemptsPrivateMethod(null, 1);
    }

    @Test(expected = NoAttemptsLeftException.class)
    public void validateAttemptsNoAttemptsLeft() throws Exception {
        validateAttemptsPrivateMethod(2, 2);
    }

    private void validateAttemptsPrivateMethod(Integer allowedAttempts, Integer currentAttempts) throws Exception {
        Context context = createContext();
        List<UUID> profileIds = new ArrayList<>();
        while(profileIds.size() < currentAttempts) {
            profileIds.add(UUID.randomUUID());
        }

        Map<String, Object> setting = new HashMap();
        if (allowedAttempts != null) {
            setting.put(CollectionSetting.AttemptsAllowed.getLiteral(), new Double(allowedAttempts));
        }
        CollectionDto collectionDto = createCollectionDto(setting);
        when(collectionService.getCollectionOrAssessment(any(UUID.class), anyBoolean()))
                .thenReturn(collectionDto);
        when(contextProfileService.findContextProfileIdsByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(profileIds);

        WhiteboxImpl.invokeMethod(contextEventService, "validateAttemptsLeft", context, profileId);

        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", context.getCollectionId(), context.getIsCollection());
        verifyPrivate(contextProfileService, times(1)).invoke("findContextProfileIdsByContextIdAndProfileId", contextId, profileId);
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
        context.setClassId(classId);
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
        contextProfile.setCreatedAt(startDate);
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
        contextProfileEvent.setCreatedAt(startDate);
        return contextProfileEvent;
    }

    private StartContextEventResponseDto createStartContextEventResponseDto() {
        StartContextEventResponseDto startContextDto = new StartContextEventResponseDto();
        startContextDto.setContextId(contextId);
        startContextDto.setCollectionId(collectionId);
        startContextDto.setEvents(new ArrayList<>());
        return startContextDto;
    }

    private CollectionDto createCollectionDto(Map<String, Object> setting) {
        CollectionDto collectionDto = new CollectionDto();
        CollectionMetadataDto metadata = new CollectionMetadataDto();
        metadata.setSetting(setting);
        collectionDto.setMetadata(metadata);

        return collectionDto;
    }

    private AnswerDto createAnswerDto(String answer) {
        AnswerDto answerDto = new AnswerDto(answer);
        answerDto.setValue(answer);
        return answerDto;
    }

    private ResourceDto createResourceDto() {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resourceId);
        resourceDto.setSequence(1);
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
        when(entity.getClassId()).thenReturn(classId);
        when(entity.getIsCollection()).thenReturn(true);

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

        calculateScoreForOrderedMultipleChoiceButOneElementWrongAnswers(questionTypeEnum);
    }

    private void calculateScoreForOrderedMultipleChoiceButOneElementWrongAnswers(QuestionTypeEnum questionTypeEnum) throws Exception {
        List<AnswerDto> userAnswers = Arrays.asList(createAnswerDto("5"));
        List<AnswerDto> correctAnswers = Arrays.asList(createAnswerDto("11"));

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                questionTypeEnum.getLiteral(), userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

}