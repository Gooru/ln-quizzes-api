package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.ContextCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.ContextResourceEventContentDto;
import com.quizzes.api.core.dtos.content.EventCollectionContentDto;
import com.quizzes.api.core.dtos.content.EventResourceContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectResourceEventContentDto;
import com.quizzes.api.core.dtos.content.SessionEventContentDto;
import com.quizzes.api.core.dtos.content.UserEventContentDto;
import com.quizzes.api.core.dtos.content.VersionEventContentDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class AnalyticsContentService {

    private final static String COLLECTION_PLAY = QuizzesUtils.COLLECTION.concat(".play");
    private final static String COLLECTION_STOP = QuizzesUtils.COLLECTION.concat(".stop");
    private final static String RESOURCE_PLAY = QuizzesUtils.COLLECTION.concat(".resource.play");
    private final static String RESOURCE_STOP = QuizzesUtils.COLLECTION.concat(".resource.stop");
    private final static String START = "start";
    private final static String STOP = "stop";

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

        EventCollectionContentDto playEvent = createCollectionEventDto(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, START);
        playEvent.setEventName(COLLECTION_PLAY);
        playEvent.setStartTime(quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void collectionStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                               boolean isCollection, String token, long startTime) {

        EventCollectionContentDto stopEvent = createCollectionEventDto(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, STOP);
        stopEvent.setEventName(COLLECTION_STOP);
        stopEvent.setStartTime(startTime);
        stopEvent.setEndTime(quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(stopEvent, token);
    }


    public void resourcePlay(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                             boolean isCollection, String token, ResourceDto resource, long startTime) {

        EventResourceContentDto playEvent = createResourceEventDto(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, START, resource);
        playEvent.setEventName(RESOURCE_PLAY);
        playEvent.setStartTime(startTime);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    private EventCollectionContentDto createCollectionEventDto(UUID collectionId, UUID classId, UUID sessionId,
                                                               UUID eventId, UUID profileId, boolean isCollection,
                                                               String token, String type) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventContentDto session = createSessionEventDto(sessionId, token);
        ContextCollectionEventContentDto context = createContextCollectionEventDto(collection, classId, type);

        return EventCollectionContentDto.builder()
                .eventId(eventId)
                .session(session)
                .user(new UserEventContentDto(profileId))
                .context(context)
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(new PayloadObjectCollectionEventContentDto(true))
                .build();
    }

    private EventResourceContentDto createResourceEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                                           UUID profileId, boolean isCollection, String token, String type,
                                                           ResourceDto resource) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventContentDto session = createSessionEventDto(sessionId, token);
        ContextResourceEventContentDto context = createContextResourceEventDto(collection, classId, type, eventId,
                resource);

        return EventResourceContentDto.builder()
                .eventId(eventId)
                .session(session)
                .user(new UserEventContentDto(profileId))
                .context(context)
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(PayloadObjectResourceEventContentDto.builder().questionType(
                        quizzesUtils.getGooruQuestionType(resource.getMetadata().getType())).build())
                .build();
    }

    private CollectionDto getCollection(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollection(collectionId) : collectionService.getAssessment(collectionId);
    }

    private ContextCollectionEventContentDto createContextCollectionEventDto(CollectionDto collection, UUID classId,
                                                                             String type) {
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

    private ContextResourceEventContentDto createContextResourceEventDto(CollectionDto collection, UUID classId,
                                                                         String type, UUID eventId, ResourceDto resource) {
        return ContextResourceEventContentDto.builder()
                .contentGooruId(resource.getId())
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .parentEventId(eventId)
                .parentGooruId(UUID.fromString(collection.getId()))
                .resourceType(resource.getIsResource() ? QuizzesUtils.RESOURCE : QuizzesUtils.QUESTION)
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
