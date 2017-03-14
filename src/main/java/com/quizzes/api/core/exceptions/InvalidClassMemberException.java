package com.quizzes.api.core.exceptions;

public class InvalidClassMemberException extends RuntimeException {

    public InvalidClassMemberException() {
        super();
    }

    public InvalidClassMemberException(String message) {
        super(message);
    }

    public InvalidClassMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClassMemberException(Throwable cause) {
        super(cause);
    }

    protected InvalidClassMemberException(String message, Throwable cause, boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
