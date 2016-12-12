package com.quizzes.api.common.dto;

import java.util.UUID;

public class ProfileGetResponseDto extends CommonProfileDto {
    private UUID id;
    private String externalId;

    public ProfileGetResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
