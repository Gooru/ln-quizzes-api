package com.quizzes.api.core.services;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidClassMemberException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.util.QuizzesUtils;

@Service
public class ContextService {

    private static final String PROFILE_ID = "profileId";
    private static final String COLLECTION_ID = "collectionId";
    private static final String CLASS_ID = "classId";
    private static final String COURSE_ID = "courseId";
    private static final String UNIT_ID = "unitId";
    private static final String LESSON_ID = "lessonId";
    private static final String EVENT_SOURCE = "eventSource";
    private static final String VERSION = "version";
    private static final List<String> SUPPORTED_VERSIONS = Arrays.asList( "1" );

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ClassMemberService classMemberService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private Gson gson;

    public UUID createContext(UUID profileId, UUID collectionId, Boolean isCollection, UUID classId,
                              ContextDataDto contextDataDto, String authToken)
            throws InvalidRequestException, InvalidAssigneeException, InvalidOwnerException, InternalServerException {

        //
        // check if version of the context create request supported
        // 
        if (!isVersionSupported(contextDataDto.getContextMap())) {
            // throw unsupported exception and quit
            throw new InvalidRequestException(
                "Version of context object requested is unsupported!");
        }

        if (classId != null) {
            QuizzesUtils.rejectAnonymous(profileId, "Anonymous users cannot create Contexts mapped to a Class");
            ClassMemberContentDto classMemberContent = classMemberService.getClassMemberContent(classId, authToken);
            if (!classMemberContent.getMemberIds().contains(profileId) && !classMemberContent.getOwnerIds()
                .contains(profileId) && !classMemberContent.getCollaborators().contains(profileId)) {
                throw new InvalidClassMemberException(
                    "Profile Id: " + profileId + " is not a valid member (Assignee or Owner) of the Class Id: "
                        + classId);
            }
            profileId = classMemberContent.getOwnerIds().get(0);
        }

        // Tries to get Collection or Assessment to verify if the ID exists.
        collectionService.getCollectionOrAssessment(collectionId, isCollection, authToken);
        String contextMapKey = generateContextMapKey(profileId, collectionId, classId, contextDataDto.getContextMap());

        // If Anonymous user then creates a Context without ContextMapKey
        if (QuizzesUtils.isAnonymous(profileId)) {
            Context context =
                    buildContext(profileId, collectionId, isCollection, classId, contextDataDto, contextMapKey);
            context.setContextMapKey(null);
            return contextRepository.save(context).getId();
        }

        ContextEntity contextEntity = contextRepository.findByContextMapKey(contextMapKey);
        if (contextEntity != null) {
            return contextEntity.getContextId();
        } else {
            Context context =
                    buildContext(profileId, collectionId, isCollection, classId, contextDataDto, contextMapKey);
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

    public ContextEntity findCreatedContext(UUID contextId, UUID profileId, String token)
        throws ContentNotFoundException {
        ContextEntity context = contextRepository.findCreatedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            context = findCreatedContextForCollaborator(contextId, profileId, token);
        }
        if (context == null) {
            throw new ContentNotFoundException(
                "Context not found for Context ID: " + contextId + " and Owner/Collaborator Profile ID: " + profileId);
        }
        return context;
    }

    public List<AssignedContextEntity> findAssignedContexts(UUID profileId) {
        return contextRepository.findAssignedContextsByProfileId(profileId);
    }

    public ContextEntity findAssignedContext(UUID contextId, UUID profileId, String token)
            throws ContentNotFoundException {
        ContextEntity context = findById(contextId);
        if (context.getClassId() != null &&
                !classMemberService.containsMemberId(context.getClassId(), profileId, token)) {
            throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                    "(member of the Class Id: " + context.getClassId() + ")");
        }
        return context;
    }

    private ContextEntity findCreatedContextForCollaborator(UUID contextId, UUID profileId, String token) {
        ContextEntity context = findById(contextId);
        if (context.getClassId() != null) {
            ClassMemberContentDto classMemberContent =
                classMemberService.getClassMemberContent(context.getClassId(), token);
            if (classMemberContent.getOwnerIds().contains(profileId) || classMemberContent.getCollaborators()
                .contains(profileId)) {
                return context;
            }
        }
        return null;
    }

    private Context buildContext(UUID profileId, UUID collectionId, boolean isCollection, UUID classId,
                                 ContextDataDto contextDataDto, String contextMapKey) {
        Context context = new Context();
        context.setProfileId(profileId);
        context.setCollectionId(collectionId);
        context.setIsCollection(isCollection);
        context.setClassId(classId);
        context.setContextData(gson.toJson(contextDataDto));
        context.setContextMapKey(contextMapKey);
        return context;
    }

    private String generateContextMapKey(UUID profileId, UUID collectionId, UUID classId,
                                         Map<String, String> contextMap) {
        String composedKey = PROFILE_ID + ":" + profileId;
        composedKey += "/" + COLLECTION_ID + ":" + collectionId;

        if (classId != null) {
            composedKey += "/" + CLASS_ID + ":" + classId;
        }

        if (contextMap.get(COURSE_ID) != null) {
            composedKey += "/" + COURSE_ID + ":" + contextMap.get(COURSE_ID);
        }

        if (contextMap.get(UNIT_ID) != null) {
            composedKey += "/" + UNIT_ID + ":" + contextMap.get(UNIT_ID);
        }

        if (contextMap.get(LESSON_ID) != null) {
            composedKey += "/" + LESSON_ID + ":" + contextMap.get(LESSON_ID);
        }

        if (contextMap.get(EVENT_SOURCE) != null) {
            composedKey += "/" + EVENT_SOURCE + ":" + contextMap.get(EVENT_SOURCE);
        }

        if (contextMap.get(VERSION) != null) {
            composedKey += "/" + VERSION + ":" + contextMap.get(VERSION);
        }

        return Base64.getEncoder().encodeToString(composedKey.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isVersionSupported(Map<String, String> contextMap) {
        if (contextMap.get(VERSION) != null) {
            return SUPPORTED_VERSIONS.contains(contextMap.get(VERSION));
        } else {
            return true;    // this older structure we support for backward compatibility
        }
    }

}
