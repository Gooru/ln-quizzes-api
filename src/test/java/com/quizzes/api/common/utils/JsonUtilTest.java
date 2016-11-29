package com.quizzes.api.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.dto.controller.ProfileIdDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.service.ContextEventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtil.class)
public class JsonUtilTest {

    @InjectMocks
    private JsonUtil jsonUtil = Mockito.spy(JsonUtil.class);

    @Mock
    Gson gson = new Gson();

    @Test
    public void removePropertyFromObject() throws Exception {
        ProfileDto profile = new ProfileDto();
        profile.setId("123");
        profile.setFirstName("test");

        JsonObject result = jsonUtil.removePropertyFromObject(profile, "id");
        assertNotNull("Result is null", result);
        assertEquals("Wrong first name", "\"test\"", result.get("firstName").toString());
        assertNull("Id is in the object", result.get("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removePropertyFromObjectNull() throws Exception {
        ProfileDto profile = new ProfileDto();
        profile.setId("123");
        profile.setFirstName("test");

        JsonObject result = jsonUtil.removePropertyFromObject(profile, null);
    }

}