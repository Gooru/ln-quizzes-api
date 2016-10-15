package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContextServiceImpl implements ContextService {

    @Autowired
    CollectionService collectionService;

    @Autowired
    ContextRepository contextRepository;

    @Override
    public Context createContext() {
        // Temporal implementation
        Context context = new Context();
        context.setId(UUID.randomUUID());
        return context;
    }

}
