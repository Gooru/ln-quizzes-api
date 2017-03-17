package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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

    public UUID createContext(UUID collectionId, UUID profileId, UUID classId, ContextDataDto contextDataDto,
                              Boolean isCollection, String token)
            throws InvalidAssigneeException, InvalidOwnerException, InternalServerException {
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
            Context context = buildContext(collectionId, collectionOwnerId, classId, contextDataDto,
                    collectionDto.getIsCollection());
            try {
                return contextRepository.save(context).getId();
            } catch (DuplicateKeyException e) {
                contextEntity = contextRepository.findByContextMapKey(contextMapKey);
                if (contextEntity == null) {
                    throw new InternalServerException("Context could not be created due to duplicated key. " +
                            "Context with ContextMapKey " + contextMapKey + " not found.");
                }
                return contextEntity.getContextId();
            }
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
    public UUID createContextWithoutClassId(UUID collectionId, UUID profileId, Boolean isCollection) {
        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(collectionId, isCollection);
        Context context = buildContextWithoutClassId(collectionId, profileId, collectionDto.getIsCollection());
        return contextRepository.save(context).getId();
    }

    public ContextEntity findById(UUID contextId) throws ContentNotFoundException {
        ContextEntity context = contextRepository.findById(contextId);
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

    public ContextEntity findAssignedContext(UUID contextId, UUID profileId, String token)
            throws ContentNotFoundException {

        ContextEntity context = findById(contextId);
        if (!classMemberService.containsMemberId(context.getClassId(), profileId, token)) {
            throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                    "(member of the Class Id: " + context.getClassId() + ")");
        }

        return context;
    }

    private Context buildContextWithoutClassId(UUID collectionId, UUID ownerProfileId, boolean isCollection) {
        Context context = new Context();
        context.setProfileId(ownerProfileId);
        context.setCollectionId(collectionId);
        context.setIsCollection(isCollection);
        return context;
    }

    private Context buildContext(UUID collectionId, UUID ownerProfileId, UUID classId, ContextDataDto contextDataDto,
                                 boolean isCollection) {
        Context context = buildContextWithoutClassId(collectionId, ownerProfileId, isCollection);
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
