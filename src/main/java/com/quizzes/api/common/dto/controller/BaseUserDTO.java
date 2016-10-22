package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BaseUserDTO {

    @NotNull(message = "ID is required")
    @Size(min=1, message = "ID is required")
    private String id;
    @NotNull(message = "Firstname is required")
    @Size(min=1, message = "Firstname is required")
    private String firstName;
    @NotNull(message = "Lastname is required")
    @Size(min=1, message = "Lastname is required")
    private String lastName;
    @NotNull(message = "Username is required")
    @Size(min=1, message = "Username is required")
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
