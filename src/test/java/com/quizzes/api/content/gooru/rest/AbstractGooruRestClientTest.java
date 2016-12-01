package com.quizzes.api.content.gooru.rest;

import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.TokenResponseDto;
import com.quizzes.api.content.gooru.dto.TokenUserRequestDto;
import com.quizzes.api.content.gooru.dto.UserDataTokenDto;
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
public class AbstractGooruRestClientTest {

    @InjectMocks
    private AbstractGooruRestClient abstractGooruRestClient = Mockito.spy(AbstractGooruRestClient.class);

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
                .postForObject(any(String.class), any(TokenUserRequestDto.class), eq(TokenResponseDto.class));

        String result = abstractGooruRestClient.generateUserToken(user);
        assertNotNull("Result is null", result);
        assertEquals("Wrong token", userToken, result);
    }

    @Test(expected = InternalServerException.class)
    public void generateUserTokenNull() throws Exception {

        doReturn(null).when(restTemplate)
                .postForObject(any(String.class), any(TokenUserRequestDto.class), eq(TokenResponseDto.class));

        String result = abstractGooruRestClient.generateUserToken(new UserDataTokenDto());
    }

}