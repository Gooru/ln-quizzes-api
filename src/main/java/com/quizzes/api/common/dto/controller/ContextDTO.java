package com.quizzes.api.common.dto.controller;

import java.util.Map;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class ContextDTO {

    private Map<String, String> context;

    public ContextDTO() {
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
