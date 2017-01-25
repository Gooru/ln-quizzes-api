package com.quizzes.api.core.dtos;

public class OnResourceEventPostRequestDto {
    private PostRequestResourceDto previousResource;

    public OnResourceEventPostRequestDto() {
    }

    public PostRequestResourceDto getPreviousResource() {
        return previousResource;
    }

    public void setPreviousResource(PostRequestResourceDto previousResource) {
        this.previousResource = previousResource;
    }
}
