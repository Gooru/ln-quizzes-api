package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.content.QuestionContentDto;
import com.quizzes.api.core.rest.clients.QuestionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionRestClient questionRestClient;

    public QuestionContentDto getQuestion(UUID questionId, String authToken) {
        return questionRestClient.getQuestion(questionId, authToken);
    }
}
