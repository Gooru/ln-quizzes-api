package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.ContextPutRequestDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextService {

    private static final String COURSE_ID = "courseId";
    private static final String UNIT_ID = "unitId";
    private static final String LESSON_ID = "lessonId";

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ClassMemberService classMemberService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private Gson gson;

    @Transactional
    public UUID createContext(UUID collectionId, UUID profileId, UUID classId, ContextDataDto contextDataDto,
                              Boolean isCollection, String token)
            throws InvalidOwnerException {
        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(collectionId, isCollection);
        UUID collectionOwnerId = collectionDto.getOwnerId();

        if (!collectionOwnerId.equals(profileId)) {
            if (!classMemberService.containsMemberId(classId, profileId, token)) {
                throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                        "(member of the Class Id: " + classId + ")");
            }

            if (!classMemberService.containsOwnerId(classId, collectionOwnerId, token)) {
                throw new InvalidOwnerException("Collection Owner ID: " + collectionOwnerId + " is not a valid Owner " +
                        "(owner of the Class Id: " + classId + ")");
            }
        }

        String contextMapKey = generateContextMapKey(collectionId, classId, contextDataDto.getContextMap());
        ContextEntity contextEntity = contextRepository.findByContextMapKey(contextMapKey);

        if (contextEntity != null) {
            return contextEntity.getContextId();
        } else {
            Context context = buildContext(collectionId, profileId, classId, contextDataDto,
                    collectionDto.getIsCollection());
            return contextRepository.save(context).getId();
        }
    }

    /**
     * Creates Contexts without ClassId and ContextMap data. Mostly used by Anonymous accounts or when a logged in
     * user does previews of a Collection (or Assessment)
     *
     * @param collectionId collection ID
     * @param profileId    we use an UUID with zeros for anonymous
     * @param isCollection defined is the collectionId corresponds to a Collection (when true) or Assessment
     * @return the context ID
     */
    @Transactional
    public UUID createContextWithoutClassId(UUID collectionId, UUID profileId, Boolean isCollection) {
        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(collectionId, isCollection);
        Context context = buildContextWithoutClassId(collectionId, profileId, collectionDto.getIsCollection());
        return contextRepository.save(context).getId();
    }

    public Context findById(UUID contextId) {
        Context context = contextRepository.findById(contextId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId);
        }
        return context;
    }

    public List<ContextEntity> findCreatedContexts(UUID profileId) {
        return contextRepository.findCreatedContextsByProfileId(profileId);
    }

    public ContextEntity findCreatedContext(UUID contextId, UUID profileId) throws ContentNotFoundException {
        ContextEntity context = contextRepository.findCreatedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Owner Profile ID: " + profileId);
        }
        return context;
    }

    public List<AssignedContextEntity> findAssignedContexts(UUID profileId) {
        return contextRepository.findAssignedContextsByProfileId(profileId);
    }

    public AssignedContextEntity findAssignedContext(UUID contextId, UUID profileId) throws ContentNotFoundException {
        AssignedContextEntity context =
                contextRepository.findAssignedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Assignee Profile ID: " + profileId);
        }
        return context;
    }

    private Context buildContextWithoutClassId(UUID collectionId, UUID profileId, boolean isCollection) {
        Context context = new Context();
        context.setProfileId(profileId);
        context.setCollectionId(collectionId);
        context.setIsCollection(isCollection);
        return context;
    }

    private Context buildContext(UUID collectionId, UUID profileId, UUID classId, ContextDataDto contextDataDto,
                                 boolean isCollection) {
        Context context = buildContextWithoutClassId(collectionId, profileId, isCollection);
        context.setClassId(classId);
        context.setContextMapKey(generateContextMapKey(collectionId, classId, contextDataDto.getContextMap()));
        context.setContextData(gson.toJson(contextDataDto));
        return context;
    }

    private String generateContextMapKey(UUID collectionId, UUID classId, Map<String, String> contextMap) {
        String composedKey = String.format("%s/%s", collectionId, classId);

        if (contextMap.get(COURSE_ID) != null) {
            composedKey += "/" + contextMap.get(COURSE_ID);
        }

        if (contextMap.get(UNIT_ID) != null) {
            composedKey += "/" + contextMap.get(UNIT_ID);
        }

        if (contextMap.get(LESSON_ID) != null) {
            composedKey += "/" + contextMap.get(LESSON_ID);
        }

        return Base64.getEncoder().encodeToString(composedKey.getBytes(StandardCharsets.UTF_8));
    }

}
