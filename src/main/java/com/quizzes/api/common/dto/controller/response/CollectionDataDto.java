package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class CollectionDataDto {
    UUID id;
    boolean isCollection;
    List<CollectionDataResourceDto> resources;

    public CollectionDataDto(UUID id, boolean isCollection, List<CollectionDataResourceDto> resources) {
        this.id = id;
        this.isCollection = isCollection;
        this.resources = resources;
    }

    public UUID getId() {
        return id;
    }

    public boolean getIsCollection() {
        return isCollection;
    }

    public List<CollectionDataResourceDto> getResources() {
        return resources;
    }
}
