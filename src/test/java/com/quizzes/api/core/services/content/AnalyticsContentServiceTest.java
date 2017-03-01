package com.quizzes.api.core.services.content;

import com.google.gson.JsonObject;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.content.ContextEventDto;
import com.quizzes.api.core.dtos.content.EventDto;
import com.quizzes.api.core.dtos.content.SessionEventDto;
import com.quizzes.api.core.dtos.content.UserEventDto;
import com.quizzes.api.core.dtos.content.VersionEventDto;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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

    private UUID collectionId;
    private UUID classId;
    private UUID contextProfileId;
    private UUID profileId;
    private UUID apiKey;
    private UUID lessonId;
    private UUID courseId;
    private UUID unitId;
    private String token;

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
    }

    @Test
    public void collectionPlay() throws Exception {
        EventDto eventDto = createEventDtoObject();
        doReturn(eventDto).when(analyticsContentService, "createEventDto", collectionId, classId,
                contextProfileId, profileId, true, token);
        doNothing().when(analyticsRestClient).play(eventDto);

        analyticsContentService.collectionPlay(collectionId, classId, contextProfileId, profileId, true, token);

        verifyPrivate(analyticsContentService, times(1))
                .invoke("createEventDto", collectionId, classId, contextProfileId, profileId, true, token);
        verify(analyticsRestClient, times(1)).play(eventDto);
    }

    @Test
    public void createSessionEventDto() throws Exception {
        doReturn(apiKey.toString()).when(configurationService).getApiKey();

        SessionEventDto result = WhiteboxImpl.invokeMethod(analyticsContentService, "createSessionEventDto",
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
    public void getCollectionForAssessment() throws Exception {
        doReturn(new CollectionDto()).when(collectionService).getAssessment(any());

        WhiteboxImpl.invokeMethod(analyticsContentService, "getCollection", collectionId, false);

        verify(collectionService, times(0)).getCollection(any());
        verify(collectionService, times(1)).getAssessment(any());
    }

    private EventDto createEventDtoObject() {
        return EventDto.builder()
                .eventId(UUID.randomUUID())
                .session(createSessionEventDtoObject())
                .user(new UserEventDto(profileId))
                .context(createContextEventDtoObject())
                .version(new VersionEventDto("3.1"))
                .startTime(1234)
                .build();
    }

    private SessionEventDto createSessionEventDtoObject() {
        return SessionEventDto.builder()
                .apiKey(apiKey)
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

    private ContextEventDto createContextEventDtoObject() {
        return ContextEventDto.builder()
                .collectionId(collectionId)
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