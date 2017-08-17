package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RubricCategoryLevelContentDto {

    @SerializedName("level_name")
    private String name;
    @SerializedName("level_score")
    private Float score;
}
