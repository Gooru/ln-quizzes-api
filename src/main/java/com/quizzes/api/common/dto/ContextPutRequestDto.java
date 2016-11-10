package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

import java.util.List;
import java.util.Map;

public class ContextPutRequestDto {

    private List<ProfileDTO> assignees;

    private MetadataDTO contextData;

    public ContextPutRequestDto() {
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
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




