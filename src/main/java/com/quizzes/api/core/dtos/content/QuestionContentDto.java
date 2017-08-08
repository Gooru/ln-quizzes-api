package com.quizzes.api.core.dtos.content;

import lombok.Data;

import java.util.UUID;

@Data
public class QuestionContentDto {

    private UUID id;
    private RubricContentDto rubric;
}
