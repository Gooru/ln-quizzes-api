package com.quizzes.api.core.dtos;

public class ExceptionMessageDto {

    private String message;
    private int status;
    private String exception;

    public ExceptionMessageDto(String message, int status, String exception) {
        this.message = message;
        this.status = status;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

}
