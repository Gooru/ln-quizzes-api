package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;

import java.util.Arrays;
import java.util.List;

public class ExtendedTextAnswerCreator extends AnswerCreatorCommon {

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
