package com.quizzes.api.common.exception;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(String message) {
        super(String.format(message));
    }

}
