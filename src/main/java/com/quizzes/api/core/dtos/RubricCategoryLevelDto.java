package com.quizzes.api.core.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RubricCategoryLevelDto {

    private String name;
    private Float score;

}
