package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDTO;

import java.util.Map;
import java.util.UUID;

public class CommonContextGetResponseDto {

    private UUID id;

    private CollectionDTO collection;

    private transient ContextDataDto contextData;

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

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

    public static class ContextDataDto {

        private Map<String, String> metadata;
        private Map<String, String> contextMap;

        public ContextDataDto() {
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
