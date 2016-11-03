package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class CollectionDataDTO {
    UUID id;
    boolean isCollection;
    List<CollectionDataResourceDTO> resources;

    public CollectionDataDTO(UUID id, boolean isResource, List<CollectionDataResourceDTO> resources) {
        this.id = id;
        this.isCollection = isResource;
        this.resources = resources;
    }

    public UUID getId() {
        return id;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public List<CollectionDataResourceDTO> getResources() {
        return resources;
    }
}
