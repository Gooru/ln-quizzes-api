package com.quizzes.api.core.dtos;

import java.io.Serializable;
import java.util.UUID;

public class ResourceDto implements Serializable {

    private UUID id;

    private boolean isResource;

    private int sequence;

    private ResourceMetadataDto metadata;

    public ResourceDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getIsResource() {
        return isResource;
    }

    public void setIsResource(boolean isResource) {
        this.isResource = isResource;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public ResourceMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(ResourceMetadataDto metadata) {
        this.metadata = metadata;
    }

}
