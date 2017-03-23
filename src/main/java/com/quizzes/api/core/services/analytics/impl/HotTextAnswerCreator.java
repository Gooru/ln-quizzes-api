package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;

import java.util.ArrayList;
import java.util.List;

public abstract class HotTextAnswerCreator extends AnswerCreatorCommon {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<Integer> optionsOrder = getOptionsOrder(resource.getMetadata().getBody());
        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (String answer : userAnswers) {
            String[] answerParts = answer.split(",");

            answerObjects.add(AnswerObject.builder()
                    .answerId("0")
                    .timeStamp(answerResource.getTimeSpent())
                    .order(optionsOrder.indexOf(new Integer(answerParts[1])) + 1)
                    .status(isCorrectContains(answer, correctValues))
                    .skip(false)
                    .text(answerParts[0])
                    .build());
        }

        return answerObjects;
    }

    protected abstract List<Integer> getOptionsOrder(String body);
}