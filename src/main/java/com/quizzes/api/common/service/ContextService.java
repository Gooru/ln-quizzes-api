package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.ContextPutRequestDTO;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDTO;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ProfileService profileService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    CollectionService collectionService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupProfileService groupProfileService;

    public Context createContext(AssignmentDTO assignmentDTO, Lms lms) {
        //Get OwnerProfile
        Profile owner = findProfile(assignmentDTO.getOwner(), lms);

        //Create a new copy of the collection
        //TODO: Go to gooru to get the collection in transform the result into a quizzes collection
        Collection collection = new Collection(); //gooruApi.getCollection(assignmentDTO.getCollection().getId())
        collection.setOwnerProfileId(owner.getId()); //We could send this param in the previous method
        collection = collectionService.save(collection);

        //Assign teacher and students to a group
        Group group = groupService.createGroup(collection.getOwnerProfileId());
        assignProfilesToGroup(group.getId(), assignmentDTO.getAssignees(), lms);

        Context context = new Context(null, collection.getId(), group.getId(),
                new Gson().toJson(assignmentDTO.getContextData()), null);
        context = contextRepository.save(context);

        return context;
    }

    public Context update(UUID contextId, ContextPutRequestDTO contextPutRequestDTO) {
        Gson gson = new Gson();
        Context context = contextRepository.findById(contextId);
        if (context == null) {
            logger.error("Error updating context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id :" + contextId);
        }
        ContextDataDTO contextDataDTO = gson.fromJson(context.getContextData(), ContextDataDTO.class);
        contextDataDTO.setMetadata(contextPutRequestDTO.getContextData().getMetadata());
        context.setContextData(gson.toJson(contextDataDTO));
        return contextRepository.save(context);
    }

    public StartContextEventResponseDTO startContextEvent(UUID contextId, UUID profileId){
        ContextProfile contextProfile = findContextProfile(contextId, profileId);
        List<ContextProfileEvent> attempts = contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(
                contextProfile.getProfileId(), contextProfile.getCurrentResourceId());

        StartContextEventResponseDTO response = new StartContextEventResponseDTO(
                UUID.randomUUID(), null, contextProfile.getCurrentResourceId(), null);
        return response;
    }

    private ContextProfile findContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile =
                contextProfileService.findContextProfileByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            contextProfile = contextProfileService .save(new ContextProfile(null, contextId, profileId, null, null, null));
        }
        return contextProfile;
    }

    private Profile findProfile(ProfileDTO profileDTO, Lms lms) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDTO.getId(), lms);
        if (profile == null) {
            profile = profileService
                    .save(new Profile(null, profileDTO.getId(), lms, new Gson().toJson(profileDTO), null));
        }
        return profile;
    }

    private void assignProfilesToGroup(UUID groupId, List<ProfileDTO> profiles, Lms lms) {
        Profile profile = null;
        for (ProfileDTO profileDTO : profiles) {
            profile = findProfile(profileDTO, lms);
            groupProfileService.save(new GroupProfile(null, groupId, profile.getId(), null));
        }
    }

}
