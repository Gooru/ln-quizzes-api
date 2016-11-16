package com.quizzes.api.common.dto.controller.request;

import com.quizzes.api.common.dto.controller.response.AnswerDto;

import java.util.List;
import java.util.UUID;

public class ResourceDto {
    UUID id;
    long timeSpent;
    int reaction;
    List<AnswerDto> answer;

    public ResourceDto(UUID id, long timeSpent, int reaction, List<AnswerDto> answer) {
        this.id = id;
        this.timeSpent = timeSpent;
        this.reaction = reaction;
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

    public List<AnswerDto> getAnswer() {
        return answer;
    }
}
