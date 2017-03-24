package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotSpotAnswerCreator implements AnswerCreator {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<ChoiceDto> choices = resource.getMetadata().getInteraction().getChoices();
        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (int i = 0; i < choices.size(); i++) {
            ChoiceDto choice = choices.get(i);
            int index = i + 1;
            boolean isSkipped = isSkipped(choice.getValue(), userAnswers);

            answerObjects.add(AnswerObject.builder()
                    .answerId("answer_" + index)
                    .timeStamp(answerResource.getTimeSpent())
                    .order(index)
                    .status(isCorrectSkipped(choice.getValue(), correctValues, isSkipped))
                    .skip(isSkipped)
                    .text(choice.getText())
                    .build());
        }

        return answerObjects;
    }

    private boolean isSkipped(String choice, List<String> userAnswers) {
        return !userAnswers.contains(choice);
    }
}
