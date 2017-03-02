package com.quizzes.api.core.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ResourceDto implements Serializable {

    private UUID id;
    private Boolean isResource;
    private int sequence;
    private ResourceMetadataDto metadata;

}
