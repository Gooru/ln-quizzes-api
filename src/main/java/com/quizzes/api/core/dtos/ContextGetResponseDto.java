package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.ContextDataDto;

import java.util.UUID;

public class ContextGetResponseDto {

    private UUID contextId;
    private UUID collectionId;
    private Boolean isCollection;
    private UUID profileId;
    private UUID classId;
    private Boolean isActive;
    private Long startDate;
    private Long dueDate;
    private ContextDataDto contextData;
    private Long createdDate;
    private Long modifiedDate;
    private Boolean hasStarted;

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public Boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Boolean isCollection) {
        this.isCollection = isCollection;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(Boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

}
