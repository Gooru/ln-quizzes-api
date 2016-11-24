package com.quizzes.api.common.dto.controller;

import java.util.Map;

public class ContextDataDto {
    private Map<String, Object> metadata;
    private Map<String, String> contextMap;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap;
    }
}
