package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.dtos.content.AccessTokenResponseDto;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AuthorizationTokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String token = getToken(authorization);
            byte[] userInfoData = Base64.getDecoder().decode(token);//.toString().split(":")[2].split("::");
            String decodedToken = new String(userInfoData);
            String[] splits = decodedToken.split()
//            String profileId = userInfoData[0];

            authenticationRestClient.verifyAccessToken(token);

//            request.setAttribute("profileId", accessTokenResponseDto.getUserId());
//            request.setAttribute("clientId", accessTokenResponseDto.getClientId());
            request.setAttribute("token", token);
        }
        return true;
    }

    private String getToken(String authorization) throws InvalidSessionException {
        if(authorization == null){
            throw new InvalidSessionException("Wrong Authorization value, it must contain: Token <token>");
        }

        String[] sessionToken = authorization.split(" ");
        if (sessionToken.length != 2 || !sessionToken[0].equals("Token")) {
            throw new InvalidSessionException("Wrong Authorization value, it must contain: Token <token>");
        }
        return sessionToken[1];
    }
}