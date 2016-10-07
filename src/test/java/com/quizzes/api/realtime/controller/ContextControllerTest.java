package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.realtime.model.CollectionOnAir;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    @Test
    public void mapContextCreate() throws Exception {
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("classId", "1");
        contextDTO.setContext(properties);

        when(contextService.getContext("externalId", contextDTO)).thenReturn(new ResponseEntity<> (contextMock, HttpStatus.CREATED));

        ResponseEntity<?> result = controller.mapContext("externalId", contextDTO);
        verify(contextService, times(1)).getContext(Mockito.eq("externalId"), Mockito.eq(contextDTO));
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 201);
        assertEquals(result.getBody().toString(), "{contextId=8dc0dddb-f6c2-4884-97ed-66318a9958db}");
    }

    @Test
    public void mapContextGet() throws Exception {
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("classId", "1");
        contextDTO.setContext(properties);

        when(contextService.getContext("externalId", contextDTO)).thenReturn(new ResponseEntity<> (contextMock, HttpStatus.OK));

        ResponseEntity<?> result = controller.mapContext("externalId", contextDTO);
        verify(contextService, times(1)).getContext(Mockito.eq("externalId"), Mockito.eq(contextDTO));
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody().toString(), "{contextId=8dc0dddb-f6c2-4884-97ed-66318a9958db}");
    }

}