package com.quizzes.api.common.exception;

public class InactiveContextException extends RuntimeException {

    public InactiveContextException() {
        super();
    }

    public InactiveContextException(String message) {
        super(message);
    }

    public InactiveContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public InactiveContextException(Throwable cause) {
        super(cause);
    }

}
