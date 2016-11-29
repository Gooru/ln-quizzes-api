package com.quizzes.api.common.dto;

public class OnResourceEventPostRequestDto {
    private ResourcePostRequestDto previousResource;

    public OnResourceEventPostRequestDto() {
    }

    public ResourcePostRequestDto getPreviousResource() {
        return previousResource;
    }

    public void setPreviousResource(ResourcePostRequestDto previousResource) {
        this.previousResource = previousResource;
    }
}
