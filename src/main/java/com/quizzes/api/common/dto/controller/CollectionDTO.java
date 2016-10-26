package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CollectionDTO {

    @NotNull(message = "{collection.id.not_null}")
    @Size(min=1, message = "{collection.id.size}")
    private String id;

    @NotNull(message = "{collection.name.not_null}")
    @Size(min=1, message = "{collection.name.size}Name is required")
    private String name;

    @NotNull(message = "{collection.description.not_null}")
    @Size(min=1, message = "{collection.description.size}")
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
