package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class CollectionGetResponseDto {
    private UUID id;

    private boolean isCollection;

    private List<ResourceDto> resources;

    public CollectionGetResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    public List<ResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDto> resources) {
        this.resources = resources;
    }
}
