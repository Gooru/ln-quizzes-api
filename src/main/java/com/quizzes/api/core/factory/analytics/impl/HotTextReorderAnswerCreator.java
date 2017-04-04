package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import com.quizzes.api.util.QuizzesUtils;

import java.util.ArrayList;
import java.util.List;

public class HotTextReorderAnswerCreator implements AnswerCreator {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (int i = 0; i < userAnswers.size(); i++) {
            String answer = userAnswers.get(i);
            int index = i + 1;

            answerObjects.add(AnswerObject.builder()
                    .answerId("answer_" + index)
                    .timeStamp(answerResource.getTimeSpent())
                    .order(index)
                    .status(isCorrectEquals(answer, correctValues.get(i)))
                    .skip(false)
                    .text(QuizzesUtils.decodeString(answer))
                    .build());
        }

        return answerObjects;
    }
}
