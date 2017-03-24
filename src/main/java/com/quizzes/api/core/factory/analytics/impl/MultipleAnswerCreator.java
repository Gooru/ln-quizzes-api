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
public class MultipleAnswerCreator implements AnswerCreator {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<ChoiceDto> choices = resource.getMetadata().getInteraction().getChoices();
        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (int i = 0; i < choices.size(); i++) {
            ChoiceDto choice = choices.get(i);
            int index = i + 1;
            boolean isSelected = isSelected(choice.getValue(), userAnswers);

            answerObjects.add(AnswerObject.builder()
                    .answerId("answer_" + index)
                    .timeStamp(answerResource.getTimeSpent())
                    .order(index)
                    .status(isCorrectSelected(choice.getValue(), correctValues, isSelected))
                    .skip(false)
                    .text(isSelected ? "Yes" : "No")
                    .build());
        }

        return answerObjects;
    }

    private boolean isSelected(String choice, List<String> userAnswers) {
        return userAnswers.contains(choice);
    }
}
