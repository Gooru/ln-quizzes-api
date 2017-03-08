package com.quizzes.api.core.exceptions;

public class NoAttemptsLeftException extends RuntimeException {

    public NoAttemptsLeftException(String message) {
        super(message);
    }

}