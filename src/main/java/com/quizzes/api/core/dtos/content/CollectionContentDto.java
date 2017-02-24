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

    @SerializedName("owner_id")
    private UUID ownerId;
    private Boolean isCollection;
    private List<ResourceContentDto> content;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;


}
