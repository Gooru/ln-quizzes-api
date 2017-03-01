package com.quizzes.api.core.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private UUID unitId;
    @JsonIgnore
    private UUID lessonId;
    @JsonIgnore
    private UUID courseId;

}
