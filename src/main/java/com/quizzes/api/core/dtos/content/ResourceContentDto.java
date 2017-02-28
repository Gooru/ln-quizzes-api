package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ResourceContentDto implements Serializable {

    private UUID id;
    private String title;
    private String description;

    @SerializedName("content_format")
    private String contentFormat;

    @SerializedName("content_subformat")
    private String contentSubformat;
    private String url;

    @SerializedName("sequence_id")
    private int sequence;

    @SerializedName("answer")
    private List<AnswerContentDto> answers;
    private Map<String, Object> taxonomy;

}
