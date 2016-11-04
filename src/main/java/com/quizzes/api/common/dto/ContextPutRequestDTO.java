package com.quizzes.api.common.dto;

import java.util.Map;

public class ContextPutRequestDTO {

    private MetadataDTO contextData;

    public ContextPutRequestDTO() {
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




