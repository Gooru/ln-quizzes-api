package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RubricCategoryContentDto implements Serializable {

    @SerializedName("category_title")
    private String title;
    private Boolean level;
    private Boolean scoring;
    @SerializedName("required_feedback")
    private Boolean requiredFeedback;
    @SerializedName("feedback_guidance")
    private String feedback;

    private List<RubricCategoryLevelContentDto> levels;
}
