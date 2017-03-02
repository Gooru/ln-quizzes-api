package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.content.ContextEventContentDto;
import com.quizzes.api.core.dtos.content.EventContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectEventContentDto;
import com.quizzes.api.core.dtos.content.SessionEventContentDto;
import com.quizzes.api.core.dtos.content.UserEventContentDto;
import com.quizzes.api.core.dtos.content.VersionEventContentDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnalyticsContentService {

    @Autowired
    private AnalyticsRestClient analyticsRestClient;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private QuizzesUtils quizzesUtils;

    private final static String COLLECTION_PLAY = QuizzesUtils.COLLECTION.concat(".play");
    private final static String START = "start";
    private final static String STOP = "stop";

    public void collectionPlay(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                               boolean isCollection, String token) {

        EventContentDto playEvent = createEventDto(collectionId, classId, contextProfileId, profileId, isCollection, token);
        playEvent.setEventName(COLLECTION_PLAY);
        analyticsRestClient.play(playEvent);
    }

    private EventContentDto createEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID profileId,
                                           boolean isCollection, String token) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventContentDto session = createSessionEventDto(sessionId, token);
        ContextEventContentDto context = createContextEventDto(collection, classId);

        return EventContentDto.builder()
                .eventId(UUID.randomUUID())
                .session(session)
                .user(new UserEventContentDto(profileId))
                .context(context)
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(new PayloadObjectEventContentDto(true))
                .startTime(quizzesUtils.getCurrentTimestamp())
                .build();
    }

    private CollectionDto getCollection(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollection(collectionId) : collectionService.getAssessment(collectionId);
    }

    private ContextEventContentDto createContextEventDto(CollectionDto collection, UUID classId) {
        return ContextEventContentDto.builder()
                .collectionId(UUID.fromString(collection.getId()))
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION :
                        QuizzesUtils.ASSESSMENT)
                .type(START)
                .questionCount(collection.getResources().size())
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private SessionEventContentDto createSessionEventDto(UUID contextProfileId, String token) {
        return SessionEventContentDto.builder()
                .apiKey(UUID.fromString(configurationService.getApiKey()))
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

}
