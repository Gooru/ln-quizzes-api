package com.quizzes.api.core.dtos.controller;

import com.quizzes.api.core.dtos.MetadataDto;

import java.util.Map;

public class ContextDataDto {
    private MetadataDto metadata;
    private Map<String, String> contextMap;

    public MetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap;
    }
}
