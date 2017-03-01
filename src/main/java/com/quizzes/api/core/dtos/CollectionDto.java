package com.quizzes.api.core.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class CollectionDto implements Serializable {

    private String id;
    private UUID ownerId;
    private CollectionMetadataDto metadata;
    private Boolean isCollection;
    private List<ResourceDto> resources;

    @JsonIgnore
    private UUID unitId;
    @JsonIgnore
    private UUID lessonId;
    @JsonIgnore
    private UUID courseId;

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

    public UUID getUnitId() {
        return unitId;
    }

    public void setUnitId(UUID unitId) {
        this.unitId = unitId;
    }

    public UUID getLessonId() {
        return lessonId;
    }

    public void setLessonId(UUID lessonId) {
        this.lessonId = lessonId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
}
