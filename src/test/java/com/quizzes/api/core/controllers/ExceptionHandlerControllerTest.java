package com.quizzes.api.core.controllers;

import com.quizzes.api.core.exceptions.*;
import com.quizzes.api.core.dtos.ExceptionMessageDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerControllerTest {

    @InjectMocks
    private ExceptionHandlerController controller = new ExceptionHandlerController();

    @Test
    public void handleInvalidJsonPropertiesException() throws Exception {
        ArrayList<String> missingParams = new ArrayList<>();
        missingParams.add("classId");
        missingParams.add("unitId");

        MissingJsonPropertiesException exceptionMock = new MissingJsonPropertiesException(missingParams);
        ExceptionMessageDto result = controller.handleInvalidJsonPropertiesException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Missing JSON properties: classId, unitId", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid JSON", result.getMessage());
    }

    @Test
    public void handleException() throws Exception {
        Exception exceptionMock = new Exception("New Error");
        ExceptionMessageDto exceptionMessageDto = controller.handleException(exceptionMock);
        assertNotNull("Exception Message is null", exceptionMessageDto);
        assertEquals("Wrong message exception", "Internal Server Error" , exceptionMessageDto.getMessage());
        assertEquals("Wrong status code", HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionMessageDto.getStatus());
        assertEquals("Wrong exception", "New Error", exceptionMessageDto.getException());
    }

    @Test
    public void handleContentNotFoundException() throws Exception {
        ContentNotFoundException exceptionMock = new ContentNotFoundException("We couldn't find the param");
        ExceptionMessageDto result = controller.handleContentNotFoundException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "We couldn't find the param", result.getException());
        assertEquals("Wrong status code", HttpStatus.NOT_FOUND.value(), result.getStatus());
        assertEquals("Wrong message exception", "Content not found", result.getMessage());
    }

    @Test
    public void handleInvalidCredentialsException() throws Exception {
        InvalidCredentialsException exceptionMock = new InvalidCredentialsException("Invalid client credentials.");
        ExceptionMessageDto result = controller.handleInvalidCredentialsException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid client credentials.", result.getException());
        assertEquals("Wrong status code", HttpStatus.UNAUTHORIZED.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid credentials", result.getMessage());
    }

    @Test
    public void handleInvalidSessionException() throws Exception {
        InvalidSessionException exceptionMock = new InvalidSessionException("Invalid session message");
        ExceptionMessageDto result = controller.handleInvalidSessionException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid session message", result.getException());
        assertEquals("Wrong status code", HttpStatus.UNAUTHORIZED.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid Session", result.getMessage());
    }

    @Test
    public void handleInvalidOwnerException() throws Exception {
        InvalidOwnerException exceptionMock = new InvalidOwnerException("Invalid session message");
        ExceptionMessageDto result = controller.handleInvalidOwnerException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid session message", result.getException());
        assertEquals("Wrong status code", HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Wrong message exception", "The Owner is invalid", result.getMessage());
    }

    @Test
    public void handleInvalidAssigneeException() throws Exception {
        InvalidAssigneeException exceptionMock = new InvalidAssigneeException("Invalid assignee request");
        ExceptionMessageDto result = controller.handleInvalidAssigneeException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid assignee request", result.getException());
        assertEquals("Wrong status code", HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Wrong message exception", "Forbidden request", result.getMessage());
    }

    @Test
    public void handleMissingRequestParameterException() throws Exception {
        MissingServletRequestParameterException exceptionMock = new MissingServletRequestParameterException("type", "String");
        ExceptionMessageDto result = controller.handleMissingRequestParameterException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Bad Request", result.getMessage());
    }

    @Test
    public void handleInvalidRequestException() throws Exception {
        InvalidRequestException exceptionMock = new InvalidRequestException("Invalid request arguments");
        ExceptionMessageDto result = controller.handleInvalidRequestException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid request arguments", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid request", result.getMessage());
    }

    @Test
    public void handleInvalidRequestBodyException() throws Exception {
        InvalidRequestBodyException exceptionMock = new InvalidRequestBodyException("Invalid request body arguments");
        ExceptionMessageDto result = controller.handleInvalidRequestBodyException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid request body arguments", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid request body", result.getMessage());
    }
}