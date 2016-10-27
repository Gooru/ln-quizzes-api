package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Context createContext(AssignmentDTO body, Lms lms) {
        //Get owner collection
        Profile teacher = profileService.findOrCreateOwner(body.getOwner(), lms);

        //Get the collection
        Collection collection = collectionService.findOrCreateCollection(body.getCollection());

        //Assign teacher and students to a group
        Group group = groupService.createGroup(teacher);
        groupProfileService.assignAssigneesListToGroup(group, body.getAssignees());

        Context context = new Context(null, collection.getId(), group.getId(), new Gson().toJson(body.getContextData()), null);
        context = contextRepository.save(context);

        return context;
    }

}
