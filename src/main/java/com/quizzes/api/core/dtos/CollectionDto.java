package com.quizzes.api.core.dtos;

import java.util.List;

public class CollectionDto {

    private String id;
    private CollectionMetadataDto metadata;
    private List<ResourceDto> resources;


    public CollectionDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CollectionMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(CollectionMetadataDto metadata) {
        this.metadata = metadata;
    }

    public List<ResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDto> resources) {
        this.resources = resources;
    }

}
