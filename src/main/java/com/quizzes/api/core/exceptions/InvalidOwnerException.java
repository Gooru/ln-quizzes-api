package com.quizzes.api.core.exceptions;

public class InvalidOwnerException extends RuntimeException {

    public InvalidOwnerException() {
        super();
    }

    public InvalidOwnerException(String message) {
        super(message);
    }

    public InvalidOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOwnerException(Throwable cause) {
        super(cause);
    }

}
