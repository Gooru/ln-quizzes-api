package com.quizzes.api.common.dto.controller.request;

public class OnResourceEventRequestDto {
    ResourceDto previousResource;

    public OnResourceEventRequestDto(ResourceDto previousResource) {
        this.previousResource = previousResource;
    }

    public ResourceDto getPreviousResource() {
        return previousResource;
    }
}
