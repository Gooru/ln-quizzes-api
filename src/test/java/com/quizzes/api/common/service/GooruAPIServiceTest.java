package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.api.AccessDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class GooruAPIServiceTest {

    @InjectMocks
    private GooruAPIService gooruAPIService = new GooruAPIService();

    @Test
    public void getAccessToken() throws Exception {
        assertNull(gooruAPIService.TOKEN);
        gooruAPIService.getAccessToken();
        assertNotNull(gooruAPIService.TOKEN);
        assertEquals(gooruAPIService.TOKEN.getClass(), String.class);
    }

    @Test
    public void generateGooruURL() throws Exception {
        String path = "/token";

        String result = gooruAPIService.generateGooruURL(path);
        assertNotNull(result);
        assertEquals(result.getClass(), String.class);
        assertEquals(result, "http://www.gooru.org/api/nucleus-auth/v1/token");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateGooruURLException() throws Exception {
        //It should generate an exception if path does not start with '/'
        String path = "token";
        gooruAPIService.generateGooruURL(path);
    }

    @Test
    public void getAccessKey() throws Exception {
        AccessDTO result = gooruAPIService.getAccessKey();
        assertNotNull(result);
        assertEquals(result.getClass(), AccessDTO.class);
        assertEquals(result.getClient_id(), "ba956a97-ae15-11e5-a302-f8a963065976");
        assertEquals(result.getGrant_type(), "anonymous");
        assertEquals(result.getClient_key(), "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==");
    }

}