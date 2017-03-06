package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.ContextCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.EventCollectionContentDto;
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
import static org.junit.Assert.assertNotNull;
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
    private String token;
    private long currentTime;

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
        token = UUID.randomUUID().toString();
        currentTime = 1234;
    }

    @Test
    public void collectionPlay() throws Exception {
        EventCollectionContentDto eventCollectionContentDto = createEventDtoObject();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventCollectionContentDto).when(analyticsContentService, "createEventDto", collectionId, classId,
                contextProfileId, contextProfileId, profileId, true, token, "start");
        doNothing().when(analyticsRestClient).notifyEvent(eventCollectionContentDto, token);

        analyticsContentService.collectionPlay(collectionId, classId, contextProfileId, profileId, true, token);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createEventDto", collectionId, classId, contextProfileId, contextProfileId, profileId, true, token, "start");
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollectionContentDto, token);
        verify(quizzesUtils, times(1)).getCurrentTimestamp();
    }

    @Test
    public void collectionStop() throws Exception {
        EventCollectionContentDto eventCollectionContentDto = createEventDtoObject();
        doReturn(currentTime).when(quizzesUtils, "getCurrentTimestamp");
        doReturn(eventCollectionContentDto).when(analyticsContentService, "createEventDto", collectionId, classId,
                contextProfileId, contextProfileId, profileId, true, token, "stop");
        doNothing().when(analyticsRestClient).notifyEvent(eventCollectionContentDto, token);

        analyticsContentService.collectionStop(collectionId, classId, contextProfileId, profileId, true, token, 4567);

        verifyPrivate(analyticsContentService, times(1)).invoke(
                "createEventDto", collectionId, classId, contextProfileId, contextProfileId, profileId, true, token, "stop");
        verify(analyticsRestClient, times(1)).notifyEvent(eventCollectionContentDto, token);
        verify(quizzesUtils, times(1)).getCurrentTimestamp();
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
    public void createContextEventDtoForCollection() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(false);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(true);

        ContextCollectionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService, "createContextEventDto",
                collectionDto, UUID.randomUUID(), "start");
        assertEquals("Wrong number of questions", 1, result.getQuestionCount());
        assertEquals("Wrong number of questions", "collection", result.getCollectionType());
        assertEquals("Wrong number of questions", "start", result.getType());
    }

    @Test
    public void createContextEventDtoForAssessment() throws Exception {
        ResourceDto resourceDto1 = new ResourceDto();
        resourceDto1.setIsResource(true);

        ResourceDto resourceDto2 = new ResourceDto();
        resourceDto2.setIsResource(true);

        List<ResourceDto> resources = Arrays.asList(resourceDto1, resourceDto2);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setResources(resources);
        collectionDto.setIsCollection(false);

        ContextCollectionEventContentDto result = WhiteboxImpl.invokeMethod(analyticsContentService, "createContextEventDto",
                collectionDto, UUID.randomUUID(), "stop");
        assertEquals("Wrong number of questions", 2, result.getQuestionCount());
        assertEquals("Wrong number of questions", "assessment", result.getCollectionType());
        assertEquals("Wrong number of questions", "stop", result.getType());
    }

    @Test
    public void getCollectionForAssessment() throws Exception {
        doReturn(new CollectionDto()).when(collectionService).getAssessment(any());

        WhiteboxImpl.invokeMethod(analyticsContentService, "getCollection", collectionId, false);

        verify(collectionService, times(0)).getCollection(any());
        verify(collectionService, times(1)).getAssessment(any());
    }

    private EventCollectionContentDto createEventDtoObject() {
        return EventCollectionContentDto.builder()
                .eventId(UUID.randomUUID())
                .session(createSessionEventDtoObject())
                .user(new UserEventContentDto(profileId))
                .context(createContextEventDtoObject())
                .version(new VersionEventContentDto("3.1"))
                .startTime(1234)
                .build();
    }

    private SessionEventContentDto createSessionEventDtoObject() {
        return SessionEventContentDto.builder()
                .apiKey(apiKey)
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

    private ContextCollectionEventContentDto createContextEventDtoObject() {
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

}