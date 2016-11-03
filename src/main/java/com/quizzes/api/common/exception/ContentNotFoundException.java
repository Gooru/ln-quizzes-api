package com.quizzes.api.common.exception;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(String param) {
        super(String.format(ExceptionMessageTemplate.CONTEXT_NOT_FOUND, param));
    }

}
