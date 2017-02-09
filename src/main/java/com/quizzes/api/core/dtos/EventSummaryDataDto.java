package com.quizzes.api.core.dtos;

/**
 * Summary data of all the {@link com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent}
 * totalTimeSpent:  sum of the time spent in all the questions.
 * averageReaction: average of all the reaction values.
 * averageScore:    average of all the scores.
 * totalCorrect:    amount of correct Answers.
 * totalAnswered:   amount of answered questions, skipped questions doesn't count here.
 */
public class EventSummaryDataDto {

    private long    totalTimeSpent = 0;
    private short   averageReaction = 0;
    private short   averageScore = 0;
    private short   totalCorrect = 0;
    private short   totalAnswered = 0;

    public long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public short getAverageReaction() {
        return averageReaction;
    }

    public void setAverageReaction(short averageReaction) {
        this.averageReaction = averageReaction;
    }

    public short getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(short averageScore) {
        this.averageScore = averageScore;
    }

    public short getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(short totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public short getTotalAnswered() {
        return totalAnswered;
    }

    public void setTotalAnswered(short totalAnswered) {
        this.totalAnswered = totalAnswered;
    }
}
