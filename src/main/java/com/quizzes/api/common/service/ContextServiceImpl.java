package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContextServiceImpl implements ContextService {

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

    @Autowired
    CollectionContentService collectionContentService;

    @Override
    public Context createContext(AssignmentDTO assignmentDTO, Lms lms) {
        //Get OwnerProfile
        Profile owner = findProfile(assignmentDTO.getOwner(), lms);

        //Create a new copy of the collection
        //TODO: Go to gooru to get the collection in transform the result into a quizzes collection

        Collection collection =
                collectionContentService.createCollectionCopy(assignmentDTO.getCollection().getId(), owner);

        if (collection != null) {
            collection = collectionService.save(collection);

            //Assign teacher and students to a group
            Group group = groupService.createGroup(collection.getOwnerProfileId());
            assignProfilesToGroup(group.getId(), assignmentDTO.getAssignees(), lms);

            Context context = new Context(null, collection.getId(), group.getId(),
                    new Gson().toJson(assignmentDTO.getContextData()), null);
            context = contextRepository.save(context);

            return context;
        }

        return null;
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
