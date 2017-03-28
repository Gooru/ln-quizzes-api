package com.quizzes.api.core.factory.analytics;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.enums.AnswerStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface AnswerCreator {

    List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource);

    default List<String> getAnswerValues(List<AnswerDto> answers) {
        if (answers == null) {
            return Collections.emptyList();
        }
        return answers.stream().map(AnswerDto::getValue)
                .collect(Collectors.toList());
    }

    default List<String> getChoiceValues(List<ChoiceDto> choices) {
        if (choices == null) {
            return Collections.emptyList();
        }
        return choices.stream().map(ChoiceDto::getValue)
                .collect(Collectors.toList());
    }

    default AnswerStatus isCorrectContains(String choice, List<String> correctValues) {
        if (correctValues.contains(choice)) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    default AnswerStatus isCorrectEquals(String answer, String correctValue) {
        if (correctValue.equals(answer)) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    default AnswerStatus isCorrectTrimIgnoreCase(String userAnswer, String correctValue) {
        if (userAnswer.trim().equalsIgnoreCase(correctValue.trim())) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    default AnswerStatus isCorrectSkipped(String choice, List<String> correctValues, boolean isSkipped) {
        if (isSkipped) {
            return null;
        }
        return isCorrectContains(choice, correctValues);
    }

    default AnswerStatus isCorrectSelected(String choice, List<String> correctValues, boolean isSelected) {
        if (correctValues.contains(choice)) {
            return isSelected ? AnswerStatus.Correct : AnswerStatus.Incorrect;
        }
        return isSelected ? AnswerStatus.Incorrect : AnswerStatus.Correct;
    }

}
