package com.quizzes.api.core.exceptions;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }

}
