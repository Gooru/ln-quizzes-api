package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.api.AccessDTO;
import com.quizzes.api.gooru.service.GooruAPIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GooruAPIServiceTest {

    @InjectMocks
    private GooruAPIService gooruAPIService = Mockito.spy(GooruAPIService.class);

    @Mock
    private Logger logger;

    @Test
    public void getAccessToken() throws Exception {
        Mockito.doReturn("token-id").when(gooruAPIService).generateToken();
        String result = gooruAPIService.getAccessToken();
        assertNotNull(result);
        assertEquals(result, "token-id");
    }

    @Test
    public void getAccessTokenWhenExists() throws Exception {
        ReflectionTestUtils.setField(gooruAPIService, "token", "token-id");

        String result = gooruAPIService.getAccessToken();
        assertNotNull(result);
        assertEquals(result, "token-id");
    }

    @Test
    public void generateGooruURL() throws Exception {
        ReflectionTestUtils.setField(gooruAPIService, "baseURL", "http://www.gooru.org/api/nucleus-auth/v1");
        String path = "/token";

        String result = gooruAPIService.generateGooruURL(path);
        assertNotNull(result);
        assertEquals(result.getClass(), String.class);
        assertEquals(result, "http://www.gooru.org/api/nucleus-auth/v1/token");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateGooruURLException() throws Exception {
        //It should generate an exception if path does not start with '/'
        ReflectionTestUtils.setField(gooruAPIService, "baseURL", "http://www.gooru.org/api/nucleus-auth/v1");
        String path = "token";
        gooruAPIService.generateGooruURL(path);
    }

    @Test
    public void getAccessKey() throws Exception {
        ReflectionTestUtils.setField(gooruAPIService, "clientId", "ba956a97-ae15-11e5-a302-f8a963065976");
        ReflectionTestUtils.setField(gooruAPIService, "grantType", "anonymous");
        ReflectionTestUtils.setField(gooruAPIService, "clientKey", "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==");

        AccessDTO result = gooruAPIService.getAccessKey();
        assertNotNull(result);
        assertEquals(result.getClass(), AccessDTO.class);
        assertEquals(result.getClient_id(), "ba956a97-ae15-11e5-a302-f8a963065976");
        assertEquals(result.getGrant_type(), "anonymous");
        assertEquals(result.getClient_key(), "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==");
    }

}