package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
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
    public void assignContext() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        ResponseEntity<?> result = controller.assignContext(new AssignmentDTO(), Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void startContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.startContextEvent("externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void registerResource() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.onResourceEvent("resourceId", "externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.finishContextEvent("externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

}