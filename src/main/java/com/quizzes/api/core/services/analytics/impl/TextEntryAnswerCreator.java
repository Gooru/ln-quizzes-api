package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;

import java.util.ArrayList;
import java.util.List;

public class TextEntryAnswerCreator extends AnswerCreatorCommon {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (int i = 0; i < userAnswers.size(); i++) {
            String userAnswer = userAnswers.get(i);

            answerObjects.add(AnswerObject.builder()
                    .answerId("0")
                    .timeStamp(answerResource.getTimeSpent())
                    .order(i + 1)
                    .status(isCorrectTrimIgnoreCase(userAnswer, correctValues.get(i)))
                    .skip(false)
                    .text(userAnswer)
                    .build());
        }

        return answerObjects;
    }
}
