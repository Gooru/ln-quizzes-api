package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BaseUserDTO {

    @NotNull(message = "{base_user.id.not_null}")
    @Size(min=1, message = "{base_user.id.size}")
    private String id;
    @NotNull(message = "{base_user.first_name.not_null}")
    @Size(min=1, message = "{base_user.first_name.size}")
    private String firstName;
    @NotNull(message = "{base_user.last_name.not_null}")
    @Size(min=1, message = "{base_user.last_name.size}")
    private String lastName;
    @NotNull(message = "{base_user.username.not_null}")
    @Size(min=1, message = "{base_user.username.size}")
    private String username;

    public BaseUserDTO() {
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
