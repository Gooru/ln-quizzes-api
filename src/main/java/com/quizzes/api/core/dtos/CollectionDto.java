package com.quizzes.api.core.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CollectionDto implements Serializable {

    private String id;
    private UUID ownerId;
    private CollectionMetadataDto metadata;
    private Boolean isCollection;
    private List<ResourceDto> resources;

    @ApiModelProperty(hidden = true)
    private UUID unitId;
    @ApiModelProperty(hidden = true)
    private UUID lessonId;
    @ApiModelProperty(hidden = true)
    private UUID courseId;

}
