package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.dtos.analytics.ContextCollection;
import com.quizzes.api.core.dtos.analytics.ContextReaction;
import com.quizzes.api.core.dtos.analytics.ContextResource;
import com.quizzes.api.core.dtos.analytics.EventCollection;
import com.quizzes.api.core.dtos.analytics.EventReaction;
import com.quizzes.api.core.dtos.analytics.EventResource;
import com.quizzes.api.core.dtos.analytics.PayloadObjectCollection;
import com.quizzes.api.core.dtos.analytics.PayloadObjectResource;
import com.quizzes.api.core.dtos.analytics.Session;
import com.quizzes.api.core.dtos.analytics.User;
import com.quizzes.api.core.dtos.analytics.Version;
import com.quizzes.api.core.enums.AnswerStatus;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import com.quizzes.api.core.factory.analytics.AnswerCreatorFactory;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AnalyticsContentService {

    private final static String COLLECTION_PLAY = QuizzesUtils.COLLECTION.concat(".play");
    private final static String RESOURCE_PLAY = QuizzesUtils.COLLECTION.concat(".resource.play");
    private final static String REACTION_CREATE = "reaction.create";
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

    @Autowired
    private AnswerCreatorFactory answerCreatorFactory;

    public void collectionPlayStart(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                    boolean isCollection, String token, Long startTime) {

        EventCollection playEvent = createEventCollection(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, START, startTime, startTime);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void collectionPlayStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                   boolean isCollection, String token, Long startTime) {

        EventCollection stopEvent = createEventCollection(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, STOP, startTime, quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void resourcePlayStart(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                  boolean isCollection, String token, ResourceDto resource, Long startTime, UUID eventId) {

        EventResource playEvent = createEventResource(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, START, resource, null, startTime, startTime);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void resourcePlayStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                 boolean isCollection, String token, ResourceDto resource,
                                 PostRequestResourceDto answerResource, Long startTime, Long endTime, UUID eventId) {

        EventResource stopEvent = createEventResource(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, STOP, resource, answerResource, startTime, endTime);
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void reactionCreate(UUID collectionId, UUID classId, UUID contextProfileId, UUID eventId, UUID profileId,
                               boolean isCollection, String token, String reaction, Long timestamp, UUID resourceId) {

        EventReaction playEvent = createEventReaction(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, reaction, resourceId, timestamp);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    private EventCollection createEventCollection(UUID collectionId, UUID classId, UUID sessionId,
                                                  UUID eventId, UUID profileId, boolean isCollection,
                                                  String token, String type, Long startTime, Long endTime) {

        CollectionDto collection = collectionService.getCollectionOrAssessment(collectionId, isCollection);

        return EventCollection.builder()
                .eventId(eventId)
                .eventName(COLLECTION_PLAY)
                .session(createSession(sessionId, token))
                .user(new User(profileId))
                .context(createContextCollection(collection, classId, type))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .metrics(Collections.emptyMap())
                .payLoadObject(new PayloadObjectCollection(true))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private EventResource createEventResource(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                              UUID profileId, boolean isCollection, String token, String type,
                                              ResourceDto resource, PostRequestResourceDto answerResource,
                                              Long startTime, Long endTime) {

        CollectionDto collection = collectionService.getCollectionOrAssessment(collectionId, isCollection);

        return EventResource.builder()
                .eventId(eventId)
                .eventName(RESOURCE_PLAY)
                .session(createSession(sessionId, token))
                .user(new User(profileId))
                .context(createContextResource(collection, classId, type, sessionId, resource))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .metrics(Collections.emptyMap())
                .payLoadObject(createPayloadObjectResource(type, resource, answerResource))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private PayloadObjectResource createPayloadObjectResource(String type, ResourceDto resource,
                                                              PostRequestResourceDto answerResource) {
        if (type.equals(START)) {
            return PayloadObjectResource.builder()
                    .questionType(quizzesUtils.getGooruQuestionType(resource.getMetadata().getType()))
                    .build();
        }
        return PayloadObjectResource.builder()
                .questionType(quizzesUtils.getGooruQuestionType(resource.getMetadata().getType()))
                .attemptStatus(getAttemptStatus(answerResource))
                .isStudent(true)
                .taxonomyIds(getTaxonomyIds(resource))
                .answerObject(createAnswerObject(answerResource, resource))
                .build();
    }

    private Map<String, String> getTaxonomyIds(ResourceDto resource) {
        if (resource.getMetadata().getTaxonomy() == null) {
            return Collections.emptyMap();
        }

        Map<String, String> taxonomies = new HashMap<>();
        resource.getMetadata().getTaxonomy().forEach((taxonomyKey, taxonomyDescription) -> {
            Map<String, String> taxonomyValues = (Map<String, String>) taxonomyDescription;
            taxonomies.put(taxonomyKey, taxonomyValues.get("code"));
        });
        return taxonomies;
    }

    private AnswerStatus getAttemptStatus(PostRequestResourceDto userAnswer) {
        if (userAnswer.getIsSkipped()) {
            return AnswerStatus.Skipped;
        }
        return (userAnswer.getScore() == 100) ? AnswerStatus.Correct : AnswerStatus.Incorrect;
    }

    private List<AnswerObject> createAnswerObject(PostRequestResourceDto answerResource,
                                                  ResourceDto resource) {

        if (answerResource.getAnswer() == null || resource.getIsResource()) {
            return Collections.emptyList();
        }

        QuestionTypeEnum questionType = QuestionTypeEnum.getEnum(resource.getMetadata().getType());
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(questionType);
        return creator.createAnswerObjects(answerResource, resource);
    }

    private EventReaction createEventReaction(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                              UUID profileId, boolean isCollection, String token,
                                              String reaction, UUID resourceId, Long time) {

        CollectionDto collection = collectionService.getCollectionOrAssessment(collectionId, isCollection);

        return EventReaction.builder()
                .eventId(UUID.randomUUID())
                .session(createSession(sessionId, token))
                .user(new User(profileId))
                .context(createContextReaction(collection, classId, reaction, eventId, resourceId))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .eventName(REACTION_CREATE)
                .startTime(time)
                .endTime(time)
                .build();
    }

    private ContextCollection createContextCollection(CollectionDto collection, UUID classId,
                                                      String type) {
        return ContextCollection.builder()
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

    private ContextResource createContextResource(CollectionDto collection, UUID classId,
                                                  String type, UUID collectionEventId, ResourceDto resource) {
        return ContextResource.builder()
                .contentGooruId(resource.getId())
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .parentEventId(collectionEventId)
                .parentGooruId(UUID.fromString(collection.getId()))
                .resourceType(resource.getIsResource() ? QuizzesUtils.RESOURCE : QuizzesUtils.QUESTION)
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private ContextReaction createContextReaction(CollectionDto collection, UUID classId,
                                                  String reaction, UUID eventId,
                                                  UUID resourceId) {
        return ContextReaction.builder()
                .contentGooruId(resourceId)
                .parentEventId(eventId)
                .parentGooruId(UUID.fromString(collection.getId()))
                .reactionType(reaction)
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private int getQuestionCount(List<ResourceDto> resources) {
        return (int) resources.stream().filter(resource -> !resource.getIsResource()).count();
    }

    private Session createSession(UUID contextProfileId, String token) {
        return Session.builder()
                .apiKey(UUID.fromString(configurationService.getApiKey()))
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

}
