package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.enums.AnswerStatus;
import com.quizzes.api.core.services.analytics.AnswerCreator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

abstract class AnswerCreatorCommon implements AnswerCreator {

    protected List<String> getAnswerValues(List<AnswerDto> answers) {
        if (answers == null) {
            return Collections.emptyList();
        }
        return answers.stream().map(AnswerDto::getValue)
                .collect(Collectors.toList());
    }

    protected List<String> getChoiceValues(List<ChoiceDto> choices) {
        if (choices == null) {
            return Collections.emptyList();
        }
        return choices.stream().map(ChoiceDto::getValue)
                .collect(Collectors.toList());
    }

    protected AnswerStatus isCorrectContains(String choice, List<String> correctValues) {
        if (correctValues.contains(choice)) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    protected AnswerStatus isCorrectEquals(String answer, String correctValue) {
        if (correctValue.equals(answer)) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    protected AnswerStatus isCorrectTrimIgnoreCase(String userAnswer, String correctValue) {
        if (userAnswer.trim().equalsIgnoreCase(correctValue.trim())) {
            return AnswerStatus.Correct;
        }
        return AnswerStatus.Incorrect;
    }

    protected AnswerStatus isCorrectSkipped(String choice, List<String> correctValues, boolean isSkipped) {
        if (isSkipped) {
            return null;
        }
        return isCorrectContains(choice, correctValues);
    }

    protected AnswerStatus isCorrectSelected(String choice, List<String> correctValues, boolean isSelected) {
        if (correctValues.contains(choice)) {
            return isSelected ? AnswerStatus.Correct : AnswerStatus.Incorrect;
        }
        return isSelected ? AnswerStatus.Incorrect : AnswerStatus.Correct;
    }
}
