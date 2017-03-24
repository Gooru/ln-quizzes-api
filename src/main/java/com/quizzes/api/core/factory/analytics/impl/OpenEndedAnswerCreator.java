package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OpenEndedAnswerCreator implements AnswerCreator {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        return Arrays.asList(AnswerObject.builder()
                .answerId("0")
                .timeStamp(answerResource.getTimeSpent())
                .order(0)
                .skip(false)
                .text(userAnswers.get(0))
                .build());
    }
}
