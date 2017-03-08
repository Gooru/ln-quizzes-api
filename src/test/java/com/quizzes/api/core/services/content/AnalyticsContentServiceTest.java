package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.ContextCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.ContextReactionEventContentDto;
import com.quizzes.api.core.dtos.content.ContextResourceEventContentDto;
import com.quizzes.api.core.dtos.content.EventCollectionContentDto;
import com.quizzes.api.core.dtos.content.EventReactionContentDto;
import com.quizzes.api.core.dtos.content.EventResourceContentDto;
import com.quizzes.api.core.dtos.content.SessionEventContentDto;
import com.quizzes.api.core.dtos.content.UserEventContentDto;
import com.quizzes.api.core.dtos.content.VersionEventContentDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnalyticsContentService.class)
public class AnalyticsContentServiceTest {

    @InjectMocks
    private AnalyticsContentService analyticsContentService = spy(new AnalyticsContentService());

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private AnalyticsRestClient analyticsRestClient;

    @Mock
    private CollectionService collectionService;

    @Mock
    private QuizzesUtils quizzesUtils;

    private UUID collectionId;
    private UUID classId;
    private UUID contextProfileId;
    private UUID profileId;
    private UUID apiKey;
    private UUID lessonId;
    private UUID courseId;
    private UUID unitId;
    private UUID resourceId;
    private UUID eventId;
    private String token;
    private String reaction;
    private long currentTime;
    private long startTime;
    private long stopTime;

    @Before
    public void before() throws Exception {
        collectionId = UUID.randomUUID();
        classId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        apiKey = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        currentTime = 1234;
        startTime = 1234;
        stopTime = 5678;
        reaction = "1";
    }

    @Test
    public void collectionPlay() throws Exception {
        EventCollectionContentDto eventCollectionContentDto = createEventCollectionDtoObject();
        doReturn(eventCollectionContentDto).when(analyticsContentService, "createCollectionEventDto", collectionId,
                classId, contextProfileId, contextProfileId, profileId, true, token, "start", startTime, null);
        doNothing().when(analyticsRestClient).notifyEvent(eventCollectionContentDto, token);

        analyticsContentService.collectionPlayStart(collectionId, classId, contextProfileId, profileId, true, token, startTime);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createCollectionEventDto", collectionId, classId, contextProfileId, contextProfileId, profileId,
                        true, token, "start", startTime, null);
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollectionContentDto, token);
    }

    @Test
    public void resourcePlay() throws Exception {
        EventResourceContentDto eventResourceContentDto = createEventResourceDtoObject();
        ResourceDto resource = createResourceDto();
        doReturn(eventResourceContentDto).when(analyticsContentService, "createResourceEventDto", collectionId,
                classId, contextProfileId, eventId, profileId, true, token, "start", resource, null, startTime, null);
        doNothing().when(analyticsRestClient).notifyEvent(eventResourceContentDto, token);

        analyticsContentService.resourcePlayStart(collectionId, classId, contextProfileId, profileId, true, token,
                resource, startTime, eventId);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createResourceEventDto", collectionId, classId, contextProfileId, eventId, profileId,
                        true, token, "start", resource, null, startTime, null);
        verify(analyticsRestClient, times(1)).notifyEvent(eventResourceContentDto, token);
    }

    @Test
    public void collectionStop() throws Exception {
        EventCollectionContentDto eventCollectionContentDto = createEventCollectionDtoObject();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventCollectionContentDto).when(analyticsContentService, "createCollectionEventDto", collectionId,
                classId, contextProfileId, contextProfileId, profileId, true, token, "stop", startTime, currentTime);
        doNothing().when(analyticsRestClient).notifyEvent(eventCollectionContentDto, token);

        analyticsContentService.collectionPlayStop(collectionId, classId, contextProfileId, profileId, true, token, startTime);

        verifyPrivate(analyticsContentService, times(1)).invoke("createCollectionEventDto", collectionId, classId,
                contextProfileId, contextProfileId, profileId, true, token, "stop", startTime, currentTime);
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollectionContentDto, token);
        verify(quizzesUtils, times(1)).getCurrentTimestamp();
    }

    public void reactionCreate() throws Exception {
        EventReactionContentDto eventReactionContentDto = createEventReactionDtoObject();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventReactionContentDto).when(analyticsContentService, "createReactionEventDto", collectionId, classId,
                contextProfileId, eventId, profileId, true, token, reaction, resourceId);
        doNothing().when(analyticsRestClient).notifyEvent(eventReactionContentDto, token);

        analyticsContentService.reactionCreate(collectionId, classId, contextProfileId, eventId, profileId, true,
                token, reaction, resourceId, currentTime);

        verifyPrivate(analyticsContentService, times(1)).invoke("createReactionEventDto", collectionId, classId,
                contextProfileId, eventId, profileId, true, token, reaction, resourceId);
        verify(analyticsRestClient, times(1)).notifyEvent(eventReactionContentDto, token);
    }


    @Test
    public void createSessionEventDto() throws Exception {
        doReturn(apiKey.toString()).when(configurationService).getApiKey();

        SessionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService, "createSessionEventDto",
                contextProfileId, token);

        verify(configurationService, times(1)).getApiKey();
        assertEquals("Wrong apiKey", apiKey, result.getApiKey());
        assertEquals("Wrong sessionId", contextProfileId, result.getSessionId());
        assertEquals("Wrong token", token, result.getSessionToken());
    }

    @Test
    public void getCollection() throws Exception {
        doReturn(new CollectionDto()).when(collectionService).getCollection(any());

        WhiteboxImpl.invokeMethod(analyticsContentService, "getCollection", collectionId, true);

        verifyPrivate(collectionService, times(1)).invoke("getCollection", any());
        verifyPrivate(collectionService, times(0)).invoke("getAssessment", any());
    }

    @Test
    public void getQuestionCount() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(false);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        int result = WhiteboxImpl.invokeMethod(analyticsContentService, "getQuestionCount", resources);
        assertEquals("Wrong number of questions", 1, result);
    }

    @Test
    public void getFirstResource() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setId(resourceId);
        resourceDto1.setIsResource(true);
        resourceDto1.setSequence(1);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(true);
        resourceDto2.setId(UUID.randomUUID());
        resourceDto2.setSequence(2);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        ResourceDto result = WhiteboxImpl.invokeMethod(analyticsContentService, "getFirstResource", resources);
        assertEquals("Wrong number of questions", resourceId, result.getId());
        assertTrue("IsResource is false", result.getIsResource());
    }

    @Test
    public void createContextReactionEventDto() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());

        ContextReactionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextReactionEventDto", collectionDto, classId, reaction, eventId, resourceId);

        assertEquals("Wrong content gooru Id", resourceId, result.getContentGooruId());
        assertEquals("Wrong reaction type", reaction, result.getReactionType());
        assertEquals("Wrong parent gooru Id", collectionId, result.getParentGooruId());
        assertEquals("Wrong parent event Id", eventId, result.getParentEventId());
        assertNull("Collection type is not null", result.getCollectionType());
    }

    @Test
    public void createContextCollectionEventDto() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(false);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(true);

        ContextCollectionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextCollectionEventDto", collectionDto, UUID.randomUUID(), "start");
        assertEquals("Wrong number of questions", 1, result.getQuestionCount());
        assertEquals("Wrong number of questions", "collection", result.getCollectionType());
        assertEquals("Wrong number of questions", "start", result.getType());
    }

    @Test
    public void createContextCollectionEventDtoForAssessment() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(true);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(false);

        ContextCollectionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextCollectionEventDto", collectionDto, UUID.randomUUID(), "stop");
        assertEquals("Wrong number of questions", 2, result.getQuestionCount());
        assertEquals("Wrong collection type", "assessment", result.getCollectionType());
        assertEquals("Wrong type", "stop", result.getType());
    }

    @Test
    public void createContextResourceEventDtoForAssessmentAndQuestion() throws Exception {
        ResourceDto resourceDto = createResourceDto();
        resourceDto.setIsResource(false);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);

        ContextResourceEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextResourceEventDto", collectionDto, classId, "start", collectionId, resourceDto);

        assertEquals("Wrong parent event id", collectionId, result.getParentEventId());
        assertEquals("Wrong resource id", resourceId, result.getContentGooruId());
        assertEquals("Wrong class id", classId, result.getClassGooruId());
        assertEquals("Wrong resource type", "question", result.getResourceType());
        assertEquals("Wrong collection type", "assessment", result.getCollectionType());
        assertEquals("Wrong type", "start", result.getType());
    }

    @Test
    public void createContextResourceEventDtoForCollectionAndResource() throws Exception {
        ResourceDto resourceDto = createResourceDto();
        resourceDto.setIsResource(true);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        ContextResourceEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextResourceEventDto", collectionDto, classId, "start", collectionId, resourceDto);

        assertEquals("Wrong parent event id", collectionId, result.getParentEventId());
        assertEquals("Wrong resource id", resourceId, result.getContentGooruId());
        assertEquals("Wrong class id", classId, result.getClassGooruId());
        assertEquals("Wrong resource type", "resource", result.getResourceType());
        assertEquals("Wrong collection type", "collection", result.getCollectionType());
        assertEquals("Wrong type", "start", result.getType());
    }

    @Test
    public void getCollectionForAssessment() throws Exception {
        doReturn(new CollectionDto()).when(collectionService).getAssessment(any());

        WhiteboxImpl.invokeMethod(analyticsContentService, "getCollection", collectionId, false);

        verify(collectionService, times(0)).getCollection(any());
        verify(collectionService, times(1)).getAssessment(any());
    }

    private EventCollectionContentDto createEventCollectionDtoObject() {
        return EventCollectionContentDto.builder()
                .eventId(collectionId)
                .session(createSessionEventDtoObject())
                .user(new UserEventContentDto(profileId))
                .context(createContextCollectionEventDtoObject())
                .version(new VersionEventContentDto("3.1"))
                .startTime(1234)
                .build();
    }

    private EventReactionContentDto createEventReactionDtoObject() {
        return EventReactionContentDto.builder()
                .eventId(eventId)
                .session(createSessionEventDtoObject())
                .user(new UserEventContentDto(profileId))
                .context(createContextReactionEventDtoObject())
                .version(new VersionEventContentDto("3.1"))
                .startTime(currentTime)
                .build();
    }

    private EventResourceContentDto createEventResourceDtoObject() {
        return EventResourceContentDto.builder()
                .eventId(collectionId)
                .session(createSessionEventDtoObject())
                .user(new UserEventContentDto(profileId))
                .context(createContextResourceEventDtoObject())
                .version(new VersionEventContentDto("3.1"))
                .startTime(currentTime)
                .build();
    }

    private SessionEventContentDto createSessionEventDtoObject() {
        return SessionEventContentDto.builder()
                .apiKey(apiKey)
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

    private ContextCollectionEventContentDto createContextCollectionEventDtoObject() {
        return ContextCollectionEventContentDto.builder()
                .contentGooruId(collectionId)
                .collectionType("collection")
                .type("start")
                .questionCount(2)
                .unitGooruId(unitId)
                .classGooruId(classId)
                .lessonGooruId(lessonId)
                .courseGooruId(courseId)
                .build();
    }

    private ContextReactionEventContentDto createContextReactionEventDtoObject() {
        return ContextReactionEventContentDto.builder()
                .contentGooruId(resourceId)
                .reactionType(reaction)
                .unitGooruId(unitId)
                .classGooruId(classId)
                .lessonGooruId(lessonId)
                .courseGooruId(courseId)
                .build();
    }

    private ContextResourceEventContentDto createContextResourceEventDtoObject() {
        return ContextResourceEventContentDto.builder()
                .contentGooruId(resourceId)
                .collectionType("collection")
                .type("start")
                .resourceType("question")
                .parentGooruId(collectionId)
                .parentEventId(collectionId)
                .unitGooruId(unitId)
                .classGooruId(classId)
                .lessonGooruId(lessonId)
                .courseGooruId(courseId)
                .build();
    }

    private ResourceDto createResourceDto() {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resourceId);
        return resourceDto;
    }

}