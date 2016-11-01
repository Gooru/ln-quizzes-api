package com.quizzes.api.common.dto.controller;

import java.util.List;
import java.util.UUID;

public class AttemptDTO {
    UUID id;
    long timeSpent;
    int reaction;
    int score;
    List<AnswerDTO> answer;

    public AttemptDTO(UUID id, long timeSpent, int reaction, int score, List<AnswerDTO> answer) {
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

    public List<AnswerDTO> getAnswer() {
        return answer;
    }
}
