package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.util.QuizzesUtils;

import java.util.ArrayList;
import java.util.List;

public class SingleChoiceAnswerCreator extends AnswerCreatorCommon {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<String> choices = getChoiceValues(resource.getMetadata().getInteraction().getChoices());
        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (String userAnswer : userAnswers) {
            int index = choices.indexOf(userAnswer) + 1;

            answerObjects.add(AnswerObject.builder()
                    .answerId("answer_" + index)
                    .timeStamp(answerResource.getTimeSpent())
                    .order(answerObjects.size() + 1)
                    .status(isCorrectContains(userAnswer, correctValues))
                    .skip(false)
                    .text(QuizzesUtils.decodeAnswer(userAnswer))
                    .build());
        }

        return answerObjects;
    }
}
