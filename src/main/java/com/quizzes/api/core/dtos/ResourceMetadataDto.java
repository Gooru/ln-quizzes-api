package com.quizzes.api.core.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ResourceMetadataDto implements Serializable {

    private String title;
    private String description;
    private String type;
    private String url;
    private String body;
    private String thumbnail;
    private List<AnswerDto> correctAnswer;
    private InteractionDto interaction;
    private Map<String, Object> taxonomy;
    @SerializedName("display_guide")
    private Map<String, Object> displayGuide;
    private UUID ownerId;
    private String narration;

}
