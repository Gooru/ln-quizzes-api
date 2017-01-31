package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;

import java.util.List;
import java.util.UUID;

public class ContextGetResponseDto {

    private UUID id;

    private CollectionDto collection;

    private long createdDate;

    private long modifiedDate;

    private ContextDataDto contextData;

    private IdResponseDto owner;

    private List<IdResponseDto> assignees;

    private Boolean hasStarted = null;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CollectionDto getCollection() {
        return collection;
    }

    public void setCollection(CollectionDto collection) {
        this.collection = collection;
    }

    public long getCreatedDate(){
        return this.createdDate;
    }

    public void setCreatedDate(long createdDate){
        this.createdDate = createdDate;
    }

    public void setModifiedDate(long modifiedDate){
        this.modifiedDate = modifiedDate;
    }

    public long getModifiedDate(){
        return this.modifiedDate;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

    public IdResponseDto getOwner() {
        return owner;
    }

    public void setOwner(IdResponseDto owner) {
        this.owner = owner;
    }

    public List<IdResponseDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<IdResponseDto> assignees) {
        this.assignees = assignees;
    }

    public boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

}
