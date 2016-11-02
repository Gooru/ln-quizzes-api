package com.quizzes.api.common.dto.controller.request;

public class OnResourceEventRequestDTO {
    ResourceDTO previousResource;

    public OnResourceEventRequestDTO(ResourceDTO previousResource) {
        this.previousResource = previousResource;
    }

    public ResourceDTO getPreviousResource() {
        return previousResource;
    }
}
