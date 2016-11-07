package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDTO;

import java.util.Map;
import java.util.UUID;

public class CommonContextGetResponseDto {

    private UUID id;

    private CollectionDTO collection;

    private ContextDataDTO contextData;

    public CommonContextGetResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CollectionDTO getCollection() {
        return collection;
    }

    public void setCollection(CollectionDTO collection) {
        this.collection = collection;
    }

    public ContextDataDTO getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDTO contextData) {
        this.contextData = contextData;
    }

    public static class ContextDataDTO {

        private Map<String, String> metadata;
        private Map<String, String> contextMap;

        public ContextDataDTO() {
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public Map<String, String> getContextMap() {
            return contextMap;
        }

        public void setContextMap(Map<String, String> contextMap) {
            this.contextMap = contextMap;
        }
    }
}
