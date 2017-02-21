package com.quizzes.api.core.exceptions;

public class InvalidRequestBodyException extends RuntimeException {

    public InvalidRequestBodyException(String message) {
        super(message);
    }

}
