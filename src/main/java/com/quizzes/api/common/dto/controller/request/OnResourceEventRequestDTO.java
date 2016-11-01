package com.quizzes.api.common.dto.controller.request;

import com.quizzes.api.common.dto.controller.response.AnswerDTO;

import java.util.List;
import java.util.UUID;

public class OnResourceEventRequestDTO {
    UUID id;
    long timeSpent;
    int reaction;
    List<AnswerDTO> answer;

    public OnResourceEventRequestDTO(UUID id, long timeSpent, int reaction, List<AnswerDTO> answer) {
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

    public List<AnswerDTO> getAnswer() {
        return answer;
    }
}
