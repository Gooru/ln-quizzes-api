package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.content.ContextEventDto;
import com.quizzes.api.core.dtos.content.EventDto;
import com.quizzes.api.core.dtos.content.PayloadObjectEventDto;
import com.quizzes.api.core.dtos.content.SessionEventDto;
import com.quizzes.api.core.dtos.content.UserEventDto;
import com.quizzes.api.core.dtos.content.VersionEventDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnalyticsContentService {
    private final static String VERSION = "3.1";
    private final static String ASSESSMENT = "assessment";
    private final static String COLLECTION = "collection";
    private final static String START = "start";
    private final static String STOP = "stop";
    private final static String COLLECTION_PLAY = COLLECTION.concat(".play");

    @Autowired
    private AnalyticsRestClient analyticsRestClient;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private QuizzesUtils quizzesUtils;

    public void collectionPlay(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                               boolean isCollection, String token) {

        EventDto playEvent = createEventDto(collectionId, classId, contextProfileId, profileId, isCollection, token);
        playEvent.setEventName(COLLECTION_PLAY);
        analyticsRestClient.play(playEvent);
    }

    private EventDto createEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID profileId,
                                    boolean isCollection, String token) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventDto session = createSessionEventDto(sessionId, token);
        ContextEventDto context = createContextEventDto(collection, classId);

        return EventDto.builder()
                .eventId(UUID.randomUUID())
                .session(session)
                .user(new UserEventDto(profileId))
                .context(context)
                .version(new VersionEventDto(VERSION))
                .payLoadObject(new PayloadObjectEventDto(true))
                .startTime(quizzesUtils.getCurrentTimestamp())
                .build();
    }

    private CollectionDto getCollection(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollection(collectionId) : collectionService.getAssessment(collectionId);
    }

    private ContextEventDto createContextEventDto(CollectionDto collection, UUID classId) {
        return ContextEventDto.builder()
                .collectionId(UUID.fromString(collection.getId()))
                .collectionType(collection.getIsCollection() ? COLLECTION : ASSESSMENT)
                .type(START)
                .questionCount(collection.getResources().size())
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private SessionEventDto createSessionEventDto(UUID contextProfileId, String token) {
        return SessionEventDto.builder()
                .apiKey(UUID.fromString(configurationService.getApiKey()))
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

}
