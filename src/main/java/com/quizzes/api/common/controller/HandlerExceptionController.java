package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class HandlerExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingJsonPropertiesException.class)
    public ExceptionMessage handleInvalidJsonPropertiesException(MissingJsonPropertiesException e){
        return new ExceptionMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), MissingJsonPropertiesException.class.getSimpleName());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessage handleException(Exception e){
        return new ExceptionMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), Exception.class.getSimpleName());
    }

}