package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.ContextCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.EventCollectionContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.SessionEventContentDto;
import com.quizzes.api.core.dtos.content.UserEventContentDto;
import com.quizzes.api.core.dtos.content.VersionEventContentDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final static String COLLECTION_STOP = QuizzesUtils.COLLECTION.concat(".stop");
    private final static String RESOURCE_PLAY = QuizzesUtils.COLLECTION.concat(".resource.play");
    private final static String RESOURCE_STOP = QuizzesUtils.COLLECTION.concat(".resource.stop");
    private final static String START = "start";
    private final static String STOP = "stop";

    public void collectionPlay(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                               boolean isCollection, String token) {

        EventCollectionContentDto playEvent = createEventDto(collectionId, classId, contextProfileId, contextProfileId,
                profileId, isCollection, token, START);
        playEvent.setEventName(COLLECTION_PLAY);
        playEvent.setStartTime(quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void collectionStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                               boolean isCollection, String token, long startDate) {

        EventCollectionContentDto stopEvent = createEventDto(collectionId, classId, contextProfileId, contextProfileId,
                profileId, isCollection, token, STOP);
        stopEvent.setEventName(COLLECTION_STOP);
        stopEvent.setStartTime(startDate);
        stopEvent.setEndTime(quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void resourcePlay() {
        //TODO: Implement this logic
    }

    private EventCollectionContentDto createEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID eventId, UUID profileId,
                                                     boolean isCollection, String token, String type) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventContentDto session = createSessionEventDto(sessionId, token);
        ContextCollectionEventContentDto context = createContextEventDto(collection, classId, type);

        return EventCollectionContentDto.builder()
                .eventId(eventId)
                .session(session)
                .user(new UserEventContentDto(profileId))
                .context(context)
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(new PayloadObjectCollectionEventContentDto(true))
                .build();
    }

    private CollectionDto getCollection(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollection(collectionId) : collectionService.getAssessment(collectionId);
    }

    private ContextCollectionEventContentDto createContextEventDto(CollectionDto collection, UUID classId, String type) {
        return ContextCollectionEventContentDto.builder()
                .contentGooruId(UUID.fromString(collection.getId()))
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .questionCount(collection.getIsCollection() ?
                        getQuestionCount(collection.getResources()) : collection.getResources().size())
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private int getQuestionCount(List<ResourceDto> resources) {
        return (int) resources.stream().filter(resource -> !resource.getIsResource()).count();
    }

    private SessionEventContentDto createSessionEventDto(UUID contextProfileId, String token) {
        return SessionEventContentDto.builder()
                .apiKey(UUID.fromString(configurationService.getApiKey()))
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

}
