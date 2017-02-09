package com.quizzes.api.core.exceptions;

public class RespositoryException extends RuntimeException {

    public RespositoryException() {
    }

    public RespositoryException(String message) {
        super(message);
    }

    public RespositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RespositoryException(Throwable cause) {
        super(cause);
    }

    public RespositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
