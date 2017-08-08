package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.RubricDto;
import com.quizzes.api.core.rest.clients.RubricRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RubricService {

    @Autowired
    private RubricRestClient rubricRestClient;

    public RubricDto getRubric(UUID rubricId, String authToken) {
        return rubricRestClient.getRubric(rubricId, authToken);
    }
}
