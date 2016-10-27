package com.quizzes.api.common.dto.controller;

import java.util.Map;

public class ContextDataDTO {
    private Map<String, String> metadata;
    private Map<String, String> contextMap;

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
