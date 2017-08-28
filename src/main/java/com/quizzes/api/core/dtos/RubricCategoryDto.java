package com.quizzes.api.core.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class RubricCategoryDto implements Serializable {

    private String title;
    private Boolean level;
    private Boolean scoring;
    private Boolean requiredFeedback;
    private String feedback;

    @Singular
    private List<RubricCategoryLevelDto> levels;
}