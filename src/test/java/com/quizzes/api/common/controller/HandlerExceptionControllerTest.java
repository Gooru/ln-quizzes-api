package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HandlerExceptionControllerTest {

    @InjectMocks
    private HandlerExceptionController controller = new HandlerExceptionController();

    @Test
    public void handleInvalidJsonPropertiesException() throws Exception {
        ArrayList<String> missingParams = new ArrayList<>();
        missingParams.add("classId");
        missingParams.add("unitId");

        MissingJsonPropertiesException exceptionMock = new MissingJsonPropertiesException(missingParams);
        ExceptionMessage result = controller.handleInvalidJsonPropertiesException(exceptionMock);

        assertNotNull(result);
        assertEquals(result.getException(), "MissingJsonPropertiesException");
        assertEquals(result.getStatus(), 400);
        assertEquals(result.getMessage(), "Missing JSON properties: classId, unitId");
    }

    @Test
    public void handleException() throws Exception {
        Exception exceptionMock = new Exception("New Error");
        ExceptionMessage result = controller.handleException(exceptionMock);

        assertNotNull(result);
        assertEquals(result.getException(), "Exception");
        assertEquals(result.getStatus(), 400);
        assertEquals(result.getMessage(), "New Error");
    }

}