package com.quizzes.api.core.dtos;

import com.quizzes.api.core.enums.GradingType;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class RubricDto {

    private UUID id;
    private String title;
    private String description;
    private String url;
    private String thumbnail;

    private GradingType gradingType;
    private String feedback;
    private UUID tenant;
    private UUID originalCreatorId;
    private UUID originalRubricId;
    private UUID parentRubricId;
    private UUID creatorId;
    private Date createdAt;
    private UUID modifierId;
    private Date updatedAt;
    private String publishStatus;
    private Date publishDate;

    private Map<String, Object> metadata;
    private Map<String, Object> taxonomy;
    @Singular
    private List<RubricCategoryDto> categories;

    private Boolean isRemote;
    private Boolean isRubric;
    private Boolean visibleOnProfile;
    private Boolean overallFeedbackRequired;
    private Boolean isDeleted;
}
