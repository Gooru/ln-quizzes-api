package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:simplesm-context.xml" })
public class TokenServiceTest {

    @Mock
    private AuthenticationRestClient authenticationRestClient = Mockito.spy(AuthenticationRestClient.class);

    @InjectMocks
    private TokenService tokenService;

    private final String token = "TOKEN";

    @Before
    public void before() {
        tokenService = new TokenService(authenticationRestClient);
        tokenService.delete(token);
    }

    @Ignore @Test
    public void validate() throws Exception {
        when(authenticationRestClient.verifyAccessToken(token)).thenReturn(true);

        tokenService.validate(token);
        verify(authenticationRestClient, times(1)).verifyAccessToken(token);

        tokenService.validate(token);
        verify(authenticationRestClient, times(1)).verifyAccessToken(token);
    }
}
