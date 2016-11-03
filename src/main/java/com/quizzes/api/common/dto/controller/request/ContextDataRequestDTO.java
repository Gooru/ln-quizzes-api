package com.quizzes.api.common.dto.controller.request;

import com.quizzes.api.common.dto.controller.ContextDataDTO;

public class ContextDataRequestDTO {

    private ContextDataDTO contextData;

    public ContextDataRequestDTO() {
    }

    public ContextDataDTO getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDTO contextData) {
        this.contextData = contextData;
    }
}
