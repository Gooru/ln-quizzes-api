package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.ContextCollection;
import com.quizzes.api.core.dtos.analytics.ContextReaction;
import com.quizzes.api.core.dtos.analytics.ContextResource;
import com.quizzes.api.core.dtos.analytics.EventCollection;
import com.quizzes.api.core.dtos.analytics.EventReaction;
import com.quizzes.api.core.dtos.analytics.EventResource;
import com.quizzes.api.core.dtos.analytics.PayloadObjectResource;
import com.quizzes.api.core.dtos.analytics.Session;
import com.quizzes.api.core.dtos.analytics.User;
import com.quizzes.api.core.dtos.analytics.Version;
import com.quizzes.api.core.enums.AnswerStatus;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    private Long currentTime;
    private Long startTime;
    private Long stopTime;

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
        currentTime = 1234L;
        startTime = 1234L;
        stopTime = 5678L;
        reaction = "1";
    }

    @Test
    public void collectionPlay() throws Exception {
        EventCollection eventCollection = createEventCollection();
        doReturn(eventCollection).when(analyticsContentService, "createEventCollection", collectionId,
                classId, contextProfileId, contextProfileId, profileId, true, token, "start", startTime, startTime);
        doNothing().when(analyticsRestClient).notifyEvent(eventCollection, token);

        analyticsContentService.collectionPlayStart(collectionId, classId, contextProfileId, profileId, true, token,
                startTime);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createEventCollection", collectionId, classId, contextProfileId, contextProfileId, profileId,
                        true, token, "start", startTime, startTime);
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollection, token);
    }

    @Test
    public void collectionStop() throws Exception {
        EventCollection eventCollection = createEventCollection();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventCollection).when(analyticsContentService, "createEventCollection", collectionId,
                classId, contextProfileId, contextProfileId, profileId, true, token, "stop", startTime, currentTime);
        doNothing().when(analyticsRestClient).notifyEvent(eventCollection, token);

        analyticsContentService.collectionPlayStop(collectionId, classId, contextProfileId, profileId, true, token,
                startTime);

        verifyPrivate(analyticsContentService, times(1)).invoke("createEventCollection", collectionId, classId,
                contextProfileId, contextProfileId, profileId, true, token, "stop", startTime, currentTime);
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollection, token);
        verify(quizzesUtils, times(1)).getCurrentTimestamp();
    }

    @Test
    public void resourcePlay() throws Exception {
        EventResource eventResource = createEventResource();
        ResourceDto resource = createResourceDto();
        doReturn(eventResource).when(analyticsContentService, "createEventResource", collectionId,
                classId, contextProfileId, eventId, profileId, true, token, "start", resource, null, startTime,
                startTime);
        doNothing().when(analyticsRestClient).notifyEvent(eventResource, token);

        analyticsContentService.resourcePlayStart(collectionId, classId, contextProfileId, profileId, true, token,
                resource, startTime, eventId);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createEventResource", collectionId, classId, contextProfileId, eventId, profileId,
                        true, token, "start", resource, null, startTime, startTime);
        verify(analyticsRestClient, times(1)).notifyEvent(eventResource, token);
    }

    @Test
    public void resourceStop() throws Exception {
        EventResource eventResource = createEventResource();
        ResourceDto resource = createResourceDto();
        PostRequestResourceDto answerResource = createPostRequestResourceDto();

        doReturn(eventResource).when(analyticsContentService, "createEventResource", collectionId,
                classId, contextProfileId, eventId, profileId, true, token, "stop", resource, answerResource,
                startTime, stopTime);
        doNothing().when(analyticsRestClient).notifyEvent(eventResource, token);

        analyticsContentService.resourcePlayStop(collectionId, classId, contextProfileId, profileId, true, token,
                resource, answerResource, startTime, stopTime, eventId);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createEventResource", collectionId, classId, contextProfileId, eventId, profileId,
                        true, token, "stop", resource, answerResource, startTime, stopTime);
        verify(analyticsRestClient, times(1)).notifyEvent(eventResource, token);
    }

    public void reactionCreate() throws Exception {
        EventReaction eventReaction = createEventReaction();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventReaction).when(analyticsContentService, "createEventReaction", collectionId, classId,
                contextProfileId, eventId, profileId, true, token, reaction, startTime, resourceId);
        doNothing().when(analyticsRestClient).notifyEvent(eventReaction, token);

        analyticsContentService.reactionCreate(collectionId, classId, contextProfileId, eventId, profileId, true,
                token, reaction, startTime, resourceId);

        verifyPrivate(analyticsContentService, times(1)).invoke("createEventReaction", collectionId, classId,
                contextProfileId, eventId, profileId, true, token, reaction, resourceId);
        verify(analyticsRestClient, times(1)).notifyEvent(eventReaction, token);
        verify(quizzesUtils, times(1)).getCurrentTimestamp();
    }

    @Test
    public void testCreateEventCollection() throws Exception {

    }

    @Test
    public void testCreateEventResource() throws Exception {
        ResourceDto resource = createResourceDto();
        PostRequestResourceDto answerResource = createPostRequestResourceDto();
        CollectionDto collectionDto = new CollectionDto();

        doReturn(collectionDto)
                .when(collectionService).getCollectionOrAssessment(eq(collectionId), eq(true), anyString());
        doReturn(ContextResource.builder().build())
                .when(analyticsContentService, "createContextResource", any(CollectionDto.class),
                        any(UUID.class), anyString(), any(UUID.class), any(ResourceDto.class));
        doReturn(PayloadObjectResource.builder().build())
                .when(analyticsContentService, "createPayloadObjectResource", anyString(), any(ResourceDto.class),
                        any(PostRequestResourceDto.class));
        doReturn(Session.builder().build()).when(analyticsContentService, "createSession", any(UUID.class),
                anyString());

        EventResource result = WhiteboxImpl.invokeMethod(analyticsContentService, "createEventResource",
                collectionId, classId, contextProfileId, eventId, profileId, true, token, "start", resource,
                answerResource, startTime, stopTime);

        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", collectionId, true, token);
        verifyPrivate(analyticsContentService, times(1)).invoke("createContextResource", any(CollectionDto.class),
                any(UUID.class), anyString(), any(UUID.class), any(ResourceDto.class));
        verifyPrivate(analyticsContentService, times(1)).invoke("createPayloadObjectResource", anyString(),
                any(ResourceDto.class), any(PostRequestResourceDto.class));
        verifyPrivate(analyticsContentService, times(1)).invoke("createSession", any(UUID.class), anyString());

        assertEquals("Wrong apiKey", eventId, result.getEventId());
        assertEquals("Wrong event name", "collection.resource.play", result.getEventName());
        assertEquals("Wrong startTime", startTime, result.getStartTime());
        assertEquals("Wrong endTime", stopTime, result.getEndTime());
    }

    @Test
    public void testCreateEventResourceNull() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setResources(new ArrayList<>());

        doReturn(collectionDto)
                .when(collectionService).getCollectionOrAssessment(eq(collectionId), eq(true), anyString());
        doReturn(ContextResource.builder().build())
                .when(analyticsContentService, "createContextResource", any(CollectionDto.class),
                        any(UUID.class), anyString(), any(UUID.class), any(ResourceDto.class));
        doReturn(PayloadObjectResource.builder().build())
                .when(analyticsContentService, "createPayloadObjectResource", anyString(), any(ResourceDto.class),
                        any(PostRequestResourceDto.class));
        doReturn(Session.builder().build()).when(analyticsContentService, "createSession", any(UUID.class),
                anyString());

        EventResource result = WhiteboxImpl.invokeMethod(analyticsContentService, "createEventResource",
                collectionId, classId, contextProfileId, eventId, profileId, true, token, "start", null, null,
                startTime, stopTime);

        verifyPrivate(collectionService, times(1)).invoke("getCollectionOrAssessment", collectionId, true, token);
        verifyPrivate(analyticsContentService, times(1)).invoke("createContextResource", any(CollectionDto.class),
                any(UUID.class), anyString(), any(UUID.class), any(ResourceDto.class));
        verifyPrivate(analyticsContentService, times(1)).invoke("createPayloadObjectResource", anyString(),
                any(ResourceDto.class), any(PostRequestResourceDto.class));
        verifyPrivate(analyticsContentService, times(1)).invoke("createSession", any(UUID.class), anyString());

        assertEquals("Wrong apiKey", eventId, result.getEventId());
        assertEquals("Wrong event name", "collection.resource.play", result.getEventName());
        assertEquals("Wrong startTime", startTime, result.getStartTime());
        assertEquals("Wrong endTime", stopTime, result.getEndTime());
    }

    @Test
    public void createResourcePayloadObjectStart() throws Exception {

    }

    @Test
    public void createResourcePayloadObjectStop() throws Exception {

    }

    @Test
    public void getTaxonomyIds() throws Exception {

    }

    @Test
    public void getAttemptStatusSkipped() throws Exception {
        PostRequestResourceDto userAnswer = createPostRequestResourceDto();
        userAnswer.setIsSkipped(true);

        AnswerStatus result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "getAttemptStatus", userAnswer);

        assertEquals(result, AnswerStatus.Skipped);
    }

    @Test
    public void getAttemptStatusCorrect() throws Exception {
        PostRequestResourceDto userAnswer = createPostRequestResourceDto();
        userAnswer.setIsSkipped(false);
        userAnswer.setScore(100);

        AnswerStatus result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "getAttemptStatus", userAnswer);

        assertEquals(result, AnswerStatus.Correct);
    }

    @Test
    public void getAttemptStatusIncorrect() throws Exception {
        PostRequestResourceDto userAnswer = createPostRequestResourceDto();
        userAnswer.setIsSkipped(false);
        userAnswer.setScore(0);

        AnswerStatus result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "getAttemptStatus", userAnswer);

        assertEquals(result, AnswerStatus.Incorrect);
    }

    @Test
    public void createAnswerObjectEmpty() throws Exception {

    }

    @Test
    public void createAnswerObject() throws Exception {

    }

    @Test
    public void testCreateEventReaction() throws Exception {
        boolean isCollection = true;

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        doReturn(collectionDto)
                .when(collectionService).getCollectionOrAssessment(eq(collectionId), eq(isCollection), anyString());
        doReturn(createSessionEventDtoObject()).when(analyticsContentService, "createSession",
                contextProfileId, token);

        EventReaction result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createEventReaction", collectionId, classId, contextProfileId, eventId, profileId, isCollection,
                token, reaction, resourceId, currentTime);

        assertEquals(result.getContext().getParentEventId(), eventId);
        assertEquals(result.getSession().getSessionId(), contextProfileId);
        assertEquals(result.getSession().getSessionToken(), token);
        assertEquals(result.getStartTime(), currentTime);
        assertEquals(result.getEndTime(), currentTime);
        assertEquals(result.getUser().getGooruUId(), profileId);
    }

    @Test
    public void testCreateContextCollection() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(false);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(true);

        ContextCollection result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextCollection", collectionDto, UUID.randomUUID(), "start");

        assertEquals("Wrong number of questions", 1, result.getQuestionCount());
        assertEquals("Wrong number of questions", "collection", result.getCollectionType());
        assertEquals("Wrong number of questions", "start", result.getType());
    }

    @Test
    public void testCreateContextCollectionForAssessment() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(true);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(false);

        ContextCollection result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextCollection", collectionDto, UUID.randomUUID(), "stop");
        assertEquals("Wrong number of questions", 2, result.getQuestionCount());
        assertEquals("Wrong collection type", "assessment", result.getCollectionType());
        assertEquals("Wrong type", "stop", result.getType());
    }

    @Test
    public void testCreateContextResourceForAssessmentAndQuestion() throws Exception {
        ResourceDto resourceDto = createResourceDto();
        resourceDto.setIsResource(false);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);

        ContextResource result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextResource", collectionDto, classId, "start", collectionId, resourceDto);

        assertEquals("Wrong parent event id", collectionId, result.getParentEventId());
        assertEquals("Wrong resource id", resourceId, result.getContentGooruId());
        assertEquals("Wrong class id", classId, result.getClassGooruId());
        assertEquals("Wrong resource type", "question", result.getResourceType());
        assertEquals("Wrong collection type", "assessment", result.getCollectionType());
        assertEquals("Wrong type", "start", result.getType());
    }

    @Test
    public void testCreateContextResourceForCollectionAndResource() throws Exception {
        ResourceDto resourceDto = createResourceDto();
        resourceDto.setIsResource(true);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(true);

        ContextResource result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextResource", collectionDto, classId, "start", collectionId, resourceDto);

        assertEquals("Wrong parent event id", collectionId, result.getParentEventId());
        assertEquals("Wrong resource id", resourceId, result.getContentGooruId());
        assertEquals("Wrong class id", classId, result.getClassGooruId());
        assertEquals("Wrong resource type", "resource", result.getResourceType());
        assertEquals("Wrong collection type", "collection", result.getCollectionType());
        assertEquals("Wrong type", "start", result.getType());
    }

    @Test
    public void testCreateContextReaction() throws Exception {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());

        ContextReaction result = WhiteboxImpl.invokeMethod(analyticsContentService,
                "createContextReaction", collectionDto, classId, reaction, eventId, resourceId);

        assertEquals("Wrong content gooru Id", resourceId, result.getContentGooruId());
        assertEquals("Wrong reaction type", reaction, result.getReactionType());
        assertEquals("Wrong parent gooru Id", collectionId, result.getParentGooruId());
        assertEquals("Wrong parent event Id", eventId, result.getParentEventId());
        assertNull("Collection type is not null", result.getCollectionType());
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
    public void testCreateSession() throws Exception {
        doReturn(apiKey.toString()).when(configurationService).getApiKey();

        Session result = WhiteboxImpl.invokeMethod(analyticsContentService, "createSession",
                contextProfileId, token);

        verify(configurationService, times(1)).getApiKey();
        assertEquals("Wrong apiKey", apiKey, result.getApiKey());
        assertEquals("Wrong sessionId", contextProfileId, result.getSessionId());
        assertEquals("Wrong token", token, result.getSessionToken());
    }

    private EventCollection createEventCollection() {
        return EventCollection.builder()
                .eventId(collectionId)
                .session(createSessionEventDtoObject())
                .user(new User(profileId))
                .context(createContextCollectionEventDtoObject())
                .version(new Version("3.1"))
                .startTime(1234)
                .build();
    }

    private EventReaction createEventReaction() {
        return EventReaction.builder()
                .eventId(eventId)
                .session(createSessionEventDtoObject())
                .user(new User(profileId))
                .context(createContextReactionEventDtoObject())
                .version(new Version("3.1"))
                .startTime(currentTime)
                .build();
    }

    private EventResource createEventResource() {
        return EventResource.builder()
                .eventId(collectionId)
                .session(createSessionEventDtoObject())
                .user(new User(profileId))
                .context(createContextResourceEventDtoObject())
                .version(new Version("3.1"))
                .startTime(currentTime)
                .build();
    }

    private Session createSessionEventDtoObject() {
        return Session.builder()
                .apiKey(apiKey)
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

    private ContextCollection createContextCollectionEventDtoObject() {
        return ContextCollection.builder()
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

    private ContextReaction createContextReactionEventDtoObject() {
        return ContextReaction.builder()
                .contentGooruId(resourceId)
                .reactionType(reaction)
                .unitGooruId(unitId)
                .classGooruId(classId)
                .lessonGooruId(lessonId)
                .courseGooruId(courseId)
                .build();
    }

    private ContextResource createContextResourceEventDtoObject() {
        return ContextResource.builder()
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

    private PostRequestResourceDto createPostRequestResourceDto() {
        PostRequestResourceDto answerResource = new PostRequestResourceDto();
        answerResource.setResourceId(resourceId);
        return answerResource;
    }
}