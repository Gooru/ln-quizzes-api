package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
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

    @Autowired
    GooruAPIService gooruAPIService;

    @Override
    public Context createContext(AssignmentDTO body) {
        //Validate fields
        //TODO: Validation call goes here

        gooruAPIService.getAccessToken();

        //Get owner collection
        Profile teacher = profileService.findOrCreateTeacher(body.getTeacher());

        //Get the collection
        Collection collection = collectionService.findOrCreateCollection(body.getCollection());

        //Assign teacher and students to a group
        Group group = groupService.createGroup(teacher);
        groupProfileService.assignStudentListToGroup(group, body.getStudents());

        Context context = new Context(null, collection.getId(), group.getId(), new Gson().toJson(body.getContext()), null);
        context = contextRepository.save(context);

        return context;
    }

}
