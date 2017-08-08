package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RubricContentDto implements Serializable {

    private UUID id;
    private String title;
    private String description;
    private String url;
    private String thumbnail;

    private String grader;
    @SerializedName("feedback_guidance")
    private String feedback;
    private UUID tenant;
    @SerializedName("original_creator_id")
    private UUID originalCreatorId;
    @SerializedName("original_rubric_id")
    private UUID originalRubricId;
    @SerializedName("parent_rubric_id")
    private UUID parentRubricId;
    @SerializedName("creator_id")
    private UUID creatorId;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("modifier_id")
    private UUID modifierId;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("publish_status")
    private String publishStatus;
    @SerializedName("publish_date")
    private Date publishDate;

    private Map<String, Object> metadata;
    private Map<String, Object> taxonomy;
    private List<RubricCategoryContentDto> categories;

    @SerializedName("is_remote")
    private Boolean isRemote;
    @SerializedName("is_rubric")
    private Boolean isRubric;
    @SerializedName("visible_on_profile")
    private Boolean visibleOnProfile;
    @SerializedName("overall_feedback_required")
    private Boolean overallFeedbackRequired;
    @SerializedName("is_deleted")
    private Boolean isDeleted;

}
