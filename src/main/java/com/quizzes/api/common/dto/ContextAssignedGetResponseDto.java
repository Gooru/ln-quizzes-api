package com.quizzes.api.common.dto;

public class ContextAssignedGetResponseDto extends CommonContextGetResponseDto {

    private IdResponseDto owner;

    private boolean hasStarted = false;

    public ContextAssignedGetResponseDto() {}

    public IdResponseDto getOwner() {
        return owner;
    }

    public void setOwner(IdResponseDto owner) {
        this.owner = owner;
    }

    public boolean getHasStarted(){ return hasStarted;}

    public void setHasStarted(boolean hasStarted){ this.hasStarted = hasStarted;}
}
