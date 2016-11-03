package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class CollectionDataDTO {
    UUID id;
    boolean isCollection;
    List<CollectionDataResourceDTO> resources;

    public CollectionDataDTO(UUID id, boolean isCollection, List<CollectionDataResourceDTO> resources) {
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

    public List<CollectionDataResourceDTO> getResources() {
        return resources;
    }
}
