package com.quizzes.api.itsLearning;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.GroupProfileService;
import com.quizzes.api.common.service.GroupService;
import com.quizzes.api.common.service.ProfileService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//TODO: We need to change this implementation to be its learning profile
@Service
@org.springframework.context.annotation.Profile("its-learning-lms")
public class ILContextServiceImpl implements ContextService {
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
    public Context createContext(AssignmentDTO body) {
        //Validate fields
        //TODO: Validation call goes here

        //Get owner collection
        Profile teacher = profileService.findOrCreateTeacher(body.getTeacher());

        //Get the collection
        Collection collection = collectionService.findOrCreateCollection(body.getCollection());

        //Assign teacher and students to a group
        Group group = groupService.createGroup(teacher);
        groupProfileService.assignStudentListToGroup(group, body.getStudents());

        Context context = new Context(null, collection.getId(), group.getId(), new JSONObject(body.getContext()), null);
        context = contextRepository.save(context);

        return context;

    }

}
