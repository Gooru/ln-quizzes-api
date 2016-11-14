package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CollectionDTO {

    @NotNull(message = "{collection.id.not_null}")
    @Size(min=1, message = "{collection.id.size}")
    private String id;

    public CollectionDTO() {
    }

    public CollectionDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
