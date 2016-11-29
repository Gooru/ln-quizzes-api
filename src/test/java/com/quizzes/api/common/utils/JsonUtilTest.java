package com.quizzes.api.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.controller.ProfileDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonUtil.class, Gson.class})
public class JsonUtilTest {

    @InjectMocks
    private JsonUtil jsonUtil = Mockito.spy(JsonUtil.class);

    @Mock
    Gson gson;

    @Test
    public void removePropertyFromObject() throws Exception {
        ProfileDto profile = new ProfileDto();
        profile.setId("123");
        profile.setFirstName("test");

        JsonElement jsonElement = new Gson().toJsonTree(profile);
        when(gson.toJsonTree(any(JsonElement.class))).thenReturn(jsonElement);

        JsonObject result = jsonUtil.removePropertyFromObject(profile, "id");
        verify(gson, times(1)).toJsonTree(any(Object.class));
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