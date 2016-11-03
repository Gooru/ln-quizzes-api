package com.quizzes.api.common.dto;

import java.util.Map;

public class ContextPutRequestDTO {

    private MetaDataDTO contextData;

    public ContextPutRequestDTO() {
    }

    public MetaDataDTO getContextData() {
        return contextData;
    }

    public void setContextData(MetaDataDTO contextData) {
        this.contextData = contextData;
    }

    public static class MetaDataDTO{

        public MetaDataDTO() {
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




