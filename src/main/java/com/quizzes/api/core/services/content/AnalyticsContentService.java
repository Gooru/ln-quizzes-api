package com.quizzes.api.core.services.content;

import com.google.common.base.Strings;
import com.google.gson.Gson;
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
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.enums.AnswerStatus;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import com.quizzes.api.core.factory.analytics.AnswerCreatorFactory;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
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

    private static final String COLLECTION_PLAY = QuizzesUtils.COLLECTION.concat(".play");
    private static final String RESOURCE_PLAY = QuizzesUtils.COLLECTION.concat(".resource.play");
    private static final String REACTION_CREATE = "reaction.create";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String COURSE_ID = "courseId";
    private static final String UNIT_ID = "unitId";
    private static final String LESSON_ID = "lessonId";


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

    @Autowired
    private Gson gson;

    public void collectionPlayStart(ContextEntity context, ContextProfile contextProfile, String token) {
        EventCollection playEvent = createEventCollection(context, contextProfile, START,
                contextProfile.getCreatedAt().getTime(), contextProfile.getCreatedAt().getTime(), token);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void collectionPlayStop(ContextEntity context, ContextProfile contextProfile, String token) {
        EventCollection stopEvent = createEventCollection(context, contextProfile, STOP,
                contextProfile.getCreatedAt().getTime(), quizzesUtils.getCurrentTimestamp(), token);
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void resourcePlayStart(ContextEntity context, ContextProfile contextProfile, UUID eventId,
                                  ResourceDto resource, Long startTime, String token) {
        EventResource playEvent = createEventResource(context, contextProfile, eventId, START, resource, null,
                startTime, startTime, token);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void resourcePlayStop(ContextEntity context, ContextProfile contextProfile, UUID eventId,
                                 ResourceDto resource, PostRequestResourceDto answerResource, Long startTime,
                                 Long endTime, String token) {
        EventResource stopEvent = createEventResource(context, contextProfile, eventId, STOP, resource, answerResource,
                startTime, endTime, token);
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void reactionCreate(ContextEntity context, ContextProfile contextProfile, UUID eventId,
                               String reaction, Long timestamp, UUID resourceId, String token) {
        EventReaction playEvent =
                createEventReaction(context, contextProfile, eventId, reaction, resourceId, timestamp, token);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    private EventCollection createEventCollection(ContextEntity context, ContextProfile contextProfile, String type,
                                                  long startTime, long endTime, String token) {
        CollectionDto collection = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection(), token);
        return EventCollection.builder()
                .eventId(contextProfile.getId())
                .eventName(COLLECTION_PLAY)
                .session(createSession(contextProfile.getId(), token))
                .user(new User(contextProfile.getProfileId()))
                .context(createContextCollection(context, collection, type))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .metrics(Collections.emptyMap())
                .payLoadObject(new PayloadObjectCollection(true))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private ContextCollection createContextCollection(ContextEntity context, CollectionDto collection, String type) {
        ContextDataDto contextData = gson.fromJson(context.getContextData(), ContextDataDto.class);
        ContextCollection contextCollection = ContextCollection.builder()
                .contentGooruId(context.getCollectionId())
                .collectionType(context.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .questionCount(collection.getIsCollection() ?
                        getQuestionCount(collection.getResources()) : collection.getResources().size())
                .classGooruId(context.getClassId())
                .build();
        String courseId = contextData.getContextMap().get(COURSE_ID);
        String unitId = contextData.getContextMap().get(UNIT_ID);
        String lessonId = contextData.getContextMap().get(LESSON_ID);
        contextCollection.setCourseGooruId(Strings.isNullOrEmpty(courseId) ? null : UUID.fromString(courseId));
        contextCollection.setUnitGooruId(Strings.isNullOrEmpty(unitId) ? null : UUID.fromString(unitId));
        contextCollection.setLessonGooruId(Strings.isNullOrEmpty(lessonId) ? null : UUID.fromString(lessonId));
        return contextCollection;
    }

    private EventResource createEventResource(ContextEntity context, ContextProfile contextProfile, UUID eventId,
                                              String type, ResourceDto resource, PostRequestResourceDto answerResource,
                                              Long startTime, Long endTime, String token) {
        return EventResource.builder()
                .eventId(eventId)
                .eventName(RESOURCE_PLAY)
                .session(createSession(contextProfile.getId(), token))
                .user(new User(contextProfile.getProfileId()))
                .context(createContextResource(context, type, contextProfile.getId(), resource))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .metrics(Collections.emptyMap())
                .payLoadObject(createPayloadObjectResource(type, resource, answerResource))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private EventReaction createEventReaction(ContextEntity context, ContextProfile contextProfile, UUID eventId,
                                              String reaction, UUID resourceId, Long time, String token) {
        CollectionDto collection = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection(), token);
        return EventReaction.builder()
                .eventId(UUID.randomUUID())
                .session(createSession(contextProfile.getId(), token))
                .user(new User(contextProfile.getProfileId()))
                .context(createContextReaction(collection, context.getClassId(), reaction, eventId, resourceId))
                .version(new Version(configurationService.getAnalyticsVersion()))
                .eventName(REACTION_CREATE)
                .startTime(time)
                .endTime(time)
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

    private ContextResource createContextResource(ContextEntity context, String type, UUID collectionEventId,
                                                  ResourceDto resource) {
        ContextDataDto contextData = gson.fromJson(context.getContextData(), ContextDataDto.class);
        ContextResource contextResource = ContextResource.builder()
                .contentGooruId(resource.getId())
                .collectionType(context.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .parentEventId(collectionEventId)
                .parentGooruId(context.getCollectionId())
                .resourceType(resource.getIsResource() ? QuizzesUtils.RESOURCE : QuizzesUtils.QUESTION)
                .classGooruId(context.getClassId())
                .build();
        String courseId = contextData.getContextMap().get(COURSE_ID);
        String unitId = contextData.getContextMap().get(UNIT_ID);
        String lessonId = contextData.getContextMap().get(LESSON_ID);
        contextResource.setCourseGooruId(Strings.isNullOrEmpty(courseId) ? null : UUID.fromString(courseId));
        contextResource.setUnitGooruId(Strings.isNullOrEmpty(unitId) ? null : UUID.fromString(unitId));
        contextResource.setLessonGooruId(Strings.isNullOrEmpty(lessonId) ? null : UUID.fromString(lessonId));
        return contextResource;
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
