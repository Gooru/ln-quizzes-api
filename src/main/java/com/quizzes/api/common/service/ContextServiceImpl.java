package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Context createContext(AssignmentDTO assignmentDTO, Lms lms) {
        //Get OwnerProfile
        Profile owner = findProfile(assignmentDTO.getOwner(), lms);

        //Create a new copy of the collection
        //TODO: Go to gooru to get the collection in transform the result into a quizzes collection
        Collection collection = new Collection(); //gooruApi.getCollection(assignmentDTO.getCollection().getId())
        collection.setOwnerProfileId(owner.getId()); //We could send this param in the previous method
        collectionService.save(collection);

        //Assign teacher and students to a group
        Group group = groupService.createGroup(collection.getOwnerProfileId());
        groupProfileService.assignAssigneesListToGroup(group, assignmentDTO.getAssignees());

        Context context = new Context(null, collection.getId(), group.getId(),
                new Gson().toJson(assignmentDTO.getContextData()), null);
        context = contextRepository.save(context);

        return context;
    }

    private Profile findProfile(ProfileDTO profileDTO, Lms lms) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDTO.getId(), lms);
        if (profile == null) {
            profile = profileService
                    .save(new Profile(null, profileDTO.getId(), lms, new Gson().toJson(profileDTO), null));
        }
        return profile;
    }

}
