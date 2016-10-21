package com.quizzes.api.common.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

}