package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class CollectionDataDto {
    private UUID id;

    private boolean isCollection;

    private List<CollectionDataResourceDto> resources;

    public CollectionDataDto() {
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

    public void setIsCollection(boolean collection) {
        isCollection = collection;
    }

    public List<CollectionDataResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<CollectionDataResourceDto> resources) {
        this.resources = resources;
    }
}
