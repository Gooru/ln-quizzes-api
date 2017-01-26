package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.dtos.content.TokenResponseDto;
import com.quizzes.api.core.dtos.content.UserTokenRequestDto;
import com.quizzes.api.core.dtos.content.UserDataTokenDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.testng.AssertJUnit.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class AuthenticationRestClientTest {

    @InjectMocks
    private AuthenticationRestClient authenticationRestClient = Mockito.spy(AuthenticationRestClient.class);

    @Mock
    RestTemplate restTemplate;

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Test
    public void generateUserToken() throws Exception {
        UserDataTokenDto user = new UserDataTokenDto();
        user.setFirstName("Gooru");
        user.setLastName("My");
        user.setEmail("mygooru@quizzes.com");

        String userToken = UUID.randomUUID().toString();
        TokenResponseDto token = new TokenResponseDto();
        token.setToken(userToken);

        doReturn(token).when(restTemplate)
                .postForObject(any(String.class), any(UserTokenRequestDto.class), eq(TokenResponseDto.class));

        String result = authenticationRestClient.generateUserToken(user);
        assertNotNull("Result is null", result);
        assertEquals("Wrong token", userToken, result);
    }

    @Test(expected = InternalServerException.class)
    public void generateUserTokenMustThrowAnException() throws Exception {

        //Token is null, it will throw an InternalServerException
        doReturn(null).when(restTemplate)
                .postForObject(any(String.class), any(UserTokenRequestDto.class), eq(TokenResponseDto.class));

        String result = authenticationRestClient.generateUserToken(new UserDataTokenDto());
    }

}