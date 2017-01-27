package com.quizzes.api.core.dtos;

import java.util.List;

public class CollectionDto {

    private String id;

    private List<ResourceDto> resources;

    private AssessmentMetadataDto metadata;

    public CollectionDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDto> resources) {
        this.resources = resources;
    }

    public AssessmentMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(AssessmentMetadataDto metadata) {
        this.metadata = metadata;
    }
}
