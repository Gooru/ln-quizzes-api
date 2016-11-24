package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.repository.ContextProfileEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContextProfileEventService {

    @Autowired
    ContextProfileEventRepository contextProfileEventRepository;

    List<ContextProfileEvent> findEventsByContextProfileId(UUID contextProfileId){
        return contextProfileEventRepository.findEventsByContextProfileId(contextProfileId);
    }

}
