package com.quizzes.api.core.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class CollectionDto implements Serializable {

    private String id;
    private UUID ownerId;
    private CollectionMetadataDto metadata;
    private Boolean isCollection;
    private List<ResourceDto> resources;

    public CollectionDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public CollectionMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(CollectionMetadataDto metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Boolean isCollection) {
        this.isCollection = isCollection;
    }

    public List<ResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDto> resources) {
        this.resources = resources;
    }

}
