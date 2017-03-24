package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CommonResourceDto {

    private UUID resourceId;
    private long timeSpent;
    private int reaction;
    private List<AnswerDto> answer;

}