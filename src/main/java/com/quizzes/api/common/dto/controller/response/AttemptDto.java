package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class AttemptDto {
    private UUID resourceId;
    private long timeSpent;
    private int reaction;
    private int score;
    private List<AnswerDto> answer;

    public AttemptDto(){
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<AnswerDto> getAnswer() {
        return answer;
    }

    public void setAnswer(List<AnswerDto> answer) {
        this.answer = answer;
    }
}
