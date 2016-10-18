package com.quizzes.api.itsLearning;

import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

//TODO: We need to change this implementation to be its learning profile
@Service
@Profile("gooru-lms")
public class ILContextServiceImpl implements ContextService {
    @Override
    public Context createContext() {
        Context context = new Context();
        context.setId(UUID.randomUUID());
        return context;
    }
}
