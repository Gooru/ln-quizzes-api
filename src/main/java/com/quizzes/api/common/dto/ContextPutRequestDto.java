package com.quizzes.api.common.dto;

import java.util.List;

public class ContextPutRequestDto {

    private List<ProfileDto> assignees;

    private PutRequestMetadataDTO contextData;

    public ContextPutRequestDto() {
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDto> assignees) {
        this.assignees = assignees;
    }

    public PutRequestMetadataDTO getContextData() {
        return contextData;
    }

    public void setContextData(PutRequestMetadataDTO contextData) {
        this.contextData = contextData;
    }

    public static class PutRequestMetadataDTO {

        public PutRequestMetadataDTO() {
        }

        private MetadataDto metadata;

        public MetadataDto getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataDto metadata) {
            this.metadata = metadata;
        }
    }

}
