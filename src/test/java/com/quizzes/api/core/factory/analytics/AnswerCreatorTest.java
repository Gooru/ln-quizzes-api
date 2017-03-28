package com.quizzes.api.core.factory.analytics;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.enums.AnswerStatus;
import com.quizzes.api.core.factory.analytics.impl.UnknownAnswerCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnswerCreator.class)
public class AnswerCreatorTest {

    private AnswerCreator creator = spy(new UnknownAnswerCreator());

    @Test
    public void getAnswerValuesNull() {
        List<String> extractedValues = creator.getAnswerValues(null);
        assertEquals(0, extractedValues.size());
    }

    @Test
    public void getAnswerValues() {
        List<String> values = Arrays.asList("A", "B");
        List<AnswerDto> answerDtos = createAnswerDtos(values);

        List<String> extractedValues = creator.getAnswerValues(answerDtos);
        assertEquals(values, extractedValues);
    }

    @Test
    public void getChoiceValuesNull() {
        List<String> extractedValues = creator.getChoiceValues(null);
        assertEquals(0, extractedValues.size());
    }

    @Test
    public void getChoiceValues() {
        List<String> values = Arrays.asList("A", "B");
        List<ChoiceDto> answerDtos = createChoiceDtos(values);

        List<String> extractedValues = creator.getChoiceValues(answerDtos);
        assertEquals(values, extractedValues);
    }

    @Test
    public void isCorrectContainsCorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String answer = "A";

        AnswerStatus status = creator.isCorrectContains(answer, correctValues);
        assertEquals(AnswerStatus.Correct, status);
    }

    @Test
    public void isCorrectContainsIncorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String answer = "C";

        AnswerStatus status = creator.isCorrectContains(answer, correctValues);
        assertEquals(AnswerStatus.Incorrect, status);
    }

    @Test
    public void isCorrectEqualsCorrect() {
        String correctValue = "A";
        String answer = "A";

        AnswerStatus status = creator.isCorrectEquals(answer, correctValue);
        assertEquals(AnswerStatus.Correct, status);
    }

    @Test
    public void isCorrectEqualsIncorrect() {
        String correctValue = "A";
        String answer = "B";

        AnswerStatus status = creator.isCorrectEquals(answer, correctValue);
        assertEquals(AnswerStatus.Incorrect, status);
    }

    @Test
    public void isCorrectTrimIgnoreCaseCorrect() {
        String correctValue = "A";
        String answer = " a ";

        AnswerStatus status = creator.isCorrectTrimIgnoreCase(answer, correctValue);
        assertEquals(AnswerStatus.Correct, status);
    }

    @Test
    public void isCorrectTrimIgnoreCaseIncorrect() {
        String correctValue = "A";
        String answer = "B";

        AnswerStatus status = creator.isCorrectTrimIgnoreCase(answer, correctValue);
        assertEquals(AnswerStatus.Incorrect, status);
    }

    @Test
    public void isCorrectSkippedTrue() {
        List<String> correctValues = Arrays.asList("A", "B");
        String answer = "A";

        AnswerStatus status = creator.isCorrectSkipped(answer, correctValues, true);
        assertNull(status);
        verify(creator, times(0)).isCorrectContains(answer, correctValues);
    }

    @Test
    public void isCorrectSkippedFalse() {
        List<String> correctValues = Arrays.asList("A", "B");
        String answer = "A";

        AnswerStatus status = creator.isCorrectSkipped(answer, correctValues, false);
        assertEquals(AnswerStatus.Correct, status);
        verify(creator, times(1)).isCorrectContains(answer, correctValues);
    }

    @Test
    public void isCorrectSelectedCorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String choice = "A";

        AnswerStatus status = creator.isCorrectSelected(choice, correctValues, true);
        assertEquals(AnswerStatus.Correct, status);
    }

    @Test
    public void isCorrectSelectedIncorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String choice = "C";

        AnswerStatus status = creator.isCorrectSelected(choice, correctValues, true);
        assertEquals(AnswerStatus.Incorrect, status);
    }

    @Test
    public void isCorrectNotSelectedCorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String choice = "C";

        AnswerStatus status = creator.isCorrectSelected(choice, correctValues, false);
        assertEquals(AnswerStatus.Correct, status);
    }

    @Test
    public void isCorrectNotSelectedIncorrect() {
        List<String> correctValues = Arrays.asList("A", "B");
        String choice = "A";

        AnswerStatus status = creator.isCorrectSelected(choice, correctValues, false);
        assertEquals(AnswerStatus.Incorrect, status);
    }

    private List<AnswerDto> createAnswerDtos(List<String> values) {
        return values.stream().map(value -> new AnswerDto(value)).collect(Collectors.toList());
    }

    private List<ChoiceDto> createChoiceDtos(List<String> values) {
        List<ChoiceDto> choices = new ArrayList<>();
        for (String value : values) {
            ChoiceDto choice = new ChoiceDto();
            choice.setText(value);
            choice.setValue(value);
            choice.setSequence(choices.size() + 1);
            choice.setIsFixed(false);

            choices.add(choice);
        }
        return choices;
    }
}
