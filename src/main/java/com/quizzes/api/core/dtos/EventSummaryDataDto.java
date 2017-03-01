package com.quizzes.api.core.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * Summary data of all the {@link com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent}
 * totalTimeSpent:  sum of the time spent in all the questions.
 * averageReaction: average of all the reaction values.
 * averageScore:    average of all the scores.
 * totalCorrect:    amount of correct Answers.
 * totalAnswered:   amount of answered questions, skipped questions doesn't count here.
 */
@Data
public class EventSummaryDataDto implements Serializable {

    private long    totalTimeSpent = 0;
    private short   averageReaction = 0;
    private short   averageScore = 0;
    private short   totalCorrect = 0;
    private short   totalAnswered = 0;

}
