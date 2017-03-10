package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextService {

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ClassMemberService classMemberService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private Gson gson;

    @Transactional
    public UUID createContext(ContextPostRequestDto contextDto, UUID profileId) throws
            InvalidOwnerException {
        validateCollectionOwnerInContext(profileId, contextDto.getCollectionId(), contextDto.getIsCollection());

        Context context = createContextObject(contextDto, profileId);
        Context savedContext = contextRepository.save(context);
        return savedContext.getId();
    }

    /**
     * Only saves collectionId, profileId and isCollection for anonymous contexts
     *
     * @param collectionId collection ID
     * @param profileId    we use an UUID with zeros for anonymous
     * @return the context ID
     */
    @Transactional
    public UUID createContextWithoutClassId(UUID collectionId, UUID profileId) {
        CollectionDto collection = collectionService.getCollectionOrAssessment(collectionId);
        Context context = new Context();
        context.setCollectionId(collectionId);
        context.setProfileId(profileId);
        context.setIsCollection(collection.getIsCollection());

        Context savedContext = contextRepository.save(context);
        return savedContext.getId();
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

    public List<ContextEntity> findMappedContext(UUID classId, UUID collectionId, Map<String, String> contextMap,
                                                 UUID profileId, String token) throws InvalidAssigneeException {
        if (!classMemberService.containsMemberId(classId, profileId, token)) {
            throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                    "(member of the Class Id: " + classId + ")");
        }
        return contextRepository.findMappedContexts(classId, collectionId, contextMap);
    }

    private void validateCollectionOwnerInContext(UUID profileId, UUID collectionId, boolean isCollection)
            throws InvalidOwnerException {
        UUID ownerId = isCollection ?
                collectionService.getCollection(collectionId).getOwnerId() :
                collectionService.getAssessment(collectionId).getOwnerId();

        if (!ownerId.equals(profileId)) {
            throw new InvalidOwnerException("Profile ID: " + profileId + " is not the owner of the collection ID: " +
                    collectionId);
        }
    }

    private Context createContextObject(ContextPostRequestDto contextDto, UUID profileId) {
        Context context = new Context();
        context.setProfileId(profileId);
        context.setClassId(contextDto.getClassId());
        context.setCollectionId(contextDto.getCollectionId());
        context.setContextData(gson.toJson(contextDto.getContextData()));
        context.setIsCollection(contextDto.getIsCollection());
        return context;
    }

}
