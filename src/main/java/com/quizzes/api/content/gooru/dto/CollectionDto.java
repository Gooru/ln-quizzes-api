package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

public class CollectionDto {

    private String id;

    private String title;

    @SerializedName("content_subformat")
    private String contentSubFormat;

    @SerializedName("sequence_id")
    private int sequence;


    public CollectionDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentSubFormat() {
        return contentSubFormat;
    }

    public void setContentSubFormat(String contentSubFormat) {
        this.contentSubFormat = contentSubFormat;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
