package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.EventDTO;
import com.quizzes.api.common.dto.controller.ProfileIdDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    @Test
    public void createContext() throws Exception {
        Context context = new Context();
        context.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);;

        ResponseEntity<?> result = controller.createContext(new AssignmentDTO(), Lms.its_learning);
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody().toString(), "{contextId=8dc0dddb-f6c2-4884-97ed-66318a9958db}");
        assertEquals(result.getBody().getClass(), HashMap.class);
    }


    @Test
    public void startContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        ResponseEntity<?> result = controller.startContextEvent("externalId", requestBody);
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody().getClass(), EventDTO.class);
    }

    @Test
    public void registerResource() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        ResponseEntity<?> result = controller.onResourceEvent("resourceId", "externalId", requestBody);
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody(), null);
    }

    @Test
    public void finishContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        ResponseEntity<?> result = controller.finishContextEvent("externalId", requestBody);
        assertNotNull(result);
        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody(), null);
    }

}