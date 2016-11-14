package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileDto {

    @NotNull(message = "{profile.id.not_null}")
    @Size(min=1, message = "{profile.id.size}")
    private String id;
    @NotNull(message = "{profile.first_name.not_null}")
    @Size(min=1, message = "{profile.first_name.size}")
    private String firstName;
    @NotNull(message = "{profile.last_name.not_null}")
    @Size(min=1, message = "{profile.last_name.size}")
    private String lastName;
    @NotNull(message = "{profile.username.not_null}")
    @Size(min=1, message = "{profile.username.size}")
    private String username;

    public ProfileDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
