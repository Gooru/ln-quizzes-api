package com.quizzes.api.core.exceptions;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(String message) {
        super(message);
    }

}
