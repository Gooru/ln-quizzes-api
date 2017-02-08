package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourceContentDto {

    private String id;

    private String title;

    private String description;

    @SerializedName("content_format")
    private String contentFormat;

    @SerializedName("content_subformat")
    private String contentSubformat;

    private String url;

    @SerializedName("sequence_id")
    private int sequence;

    @SerializedName("answer")
    private List<AnswerContentDto> answers;


    public ResourceContentDto() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentSubformat() {
        return contentSubformat;
    }

    public void setContentSubformat(String contentSubformat) {
        this.contentSubformat = contentSubformat;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<AnswerContentDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerContentDto> answers) {
        this.answers = answers;
    }

    public String getContentFormat() {
        return contentFormat;
    }

    public void setContentFormat(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
