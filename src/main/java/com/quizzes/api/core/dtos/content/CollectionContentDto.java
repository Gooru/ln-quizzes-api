package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CollectionContentDto implements Serializable {

    private String id;
    private String title;
    private Boolean isCollection;
    private List<ResourceContentDto> content;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;

    @SerializedName("owner_id")
    private UUID ownerId;
    @SerializedName("unit_id")
    private UUID unitId;
    @SerializedName("lesson_id")
    private UUID lessonId;
    @SerializedName("course_id")
    private UUID courseId;
    @SerializedName("subformat")
    private String subFormat;
    
}
