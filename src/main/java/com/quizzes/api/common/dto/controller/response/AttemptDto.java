package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class AttemptDto {
    UUID id;
    long timeSpent;
    int reaction;
    int score;
    List<AnswerDto> answer;

    public AttemptDto(UUID id, long timeSpent, int reaction, int score, List<AnswerDto> answer) {
        this.id = id;
        this.timeSpent = timeSpent;
        this.reaction = reaction;
        this.score = score;
        this.answer = answer;
    }

    public UUID getId() {
        return id;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public int getReaction() {
        return reaction;
    }

    public int getScore() {
        return score;
    }

    public List<AnswerDto> getAnswer() {
        return answer;
    }
}