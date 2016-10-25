package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("standalone-lms")
public class ContextServiceImpl implements ContextService {

    @Autowired
    CollectionService collectionService;

    @Override
    public Context createContext(AssignmentDTO body, Lms lms) {
        // Temporal implementation
        Context context = new Context();
        context.setId(UUID.randomUUID());
        return context;
    }

}
