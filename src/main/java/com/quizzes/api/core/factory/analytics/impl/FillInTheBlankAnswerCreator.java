package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FillInTheBlankAnswerCreator implements AnswerCreator {

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
