package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.ContextDataDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class ContextPostRequestDto {

    @NotNull(message = "{assignment.external.collection.id.not_null}")
    @Valid
    private String externalCollectionId;

    @NotNull(message = "{assignment.owner.not_null}")
    @Valid
    private ProfileDto owner;

    @NotNull(message = "{assignment.assignees.not_null}")
    @Size(min = 1, message = "{assignment.assignees.size}")
    @Valid
    private List<ProfileDto> assignees;

    @NotNull(message = "{assignment.context_data.not_null}")
    private ContextDataDto contextData;

    public ContextPostRequestDto() {
    }

    public String getExternalCollectionId() {
        return externalCollectionId;
    }

    public void setExternalCollectionId(String externalCollectionId) {
        this.externalCollectionId = externalCollectionId;
    }

    public ProfileDto getOwner() {
        return owner;
    }

    public void setOwner(ProfileDto owner) {
        this.owner = owner;
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDto> assignees) {
        this.assignees = assignees;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

}
