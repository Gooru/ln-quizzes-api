package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDto;

import java.util.List;
import java.util.Map;

public class ContextPutRequestDto {

    private List<ProfileDto> assignees;

    private MetadataDTO contextData;

    public ContextPutRequestDto() {
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDto> assignees) {
        this.assignees = assignees;
    }

    public MetadataDTO getContextData() {
        return contextData;
    }

    public void setContextData(MetadataDTO contextData) {
        this.contextData = contextData;
    }

    public static class MetadataDTO {

        public MetadataDTO() {
        }

        private Map<String, String> metadata;

        public Map<String, String> getMetadata () {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }
    }

}




