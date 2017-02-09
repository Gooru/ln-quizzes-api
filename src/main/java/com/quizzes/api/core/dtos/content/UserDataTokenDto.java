package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

public class UserDataTokenDto {

    @SerializedName(value = "firstname", alternate = "firstName")
    private String firstName;

    @SerializedName(value = "lastname", alternate = "lastName")
    private String lastName;

    @SerializedName(value = "identity_id", alternate = "email")
    private String email;

    public UserDataTokenDto() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
