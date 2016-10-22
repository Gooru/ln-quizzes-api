package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CollectionDTO {

    @NotNull(message = "ID is required")
    @Size(min=1, message = "ID is required")
    private String id;

    @NotNull(message = "Name is required")
    @Size(min=1, message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    @Size(min=1, message = "Description is required")
    private String description;

    public CollectionDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
