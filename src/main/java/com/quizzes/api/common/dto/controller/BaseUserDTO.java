package com.quizzes.api.common.dto.controller;

import javax.validation.constraints.NotNull;

public class BaseUserDTO {

    @NotNull
    private String id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
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
