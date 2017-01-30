package com.quizzes.api.core.dtos;

public class ResourceMetadataDto {
    private String title;
    private String type;
    private String body;
    private InteractionDto interaction;

    public ResourceMetadataDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InteractionDto getInteraction() {
        return interaction;
    }

    public void setInteraction(InteractionDto interaction) {
        this.interaction = interaction;
    }
}
