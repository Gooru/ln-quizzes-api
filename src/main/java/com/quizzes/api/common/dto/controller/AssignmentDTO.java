package com.quizzes.api.common.dto.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class AssignmentDTO {

    @NotNull(message = "{assignment.collection.not_null}")
    @Valid
    private CollectionDTO collection;
    @NotNull(message = "{assignment.owner.not_null}")
    @Valid
    private ProfileDTO owner;
    @NotNull(message = "{assignment.assignees.not_null}")
    @Size(min = 1, message = "{assignment.assignees.size}")
    @Valid
    private List<ProfileDTO> assignees;
    @NotNull(message = "{assignment.context_data.not_null}")
    private ContextDataDTO contextData;

    public AssignmentDTO() {
    }

    public CollectionDTO getCollection() {
        return collection;
    }

    public void setCollection(CollectionDTO collection) {
        this.collection = collection;
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
        this.assignees = assignees;
    }

    public ContextDataDTO getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDTO contextData) {
        this.contextData = contextData;
    }
}
