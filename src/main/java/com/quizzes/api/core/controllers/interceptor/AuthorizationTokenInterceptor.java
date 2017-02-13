package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Component
public class AuthorizationTokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        String token = getToken(authorization);
        String[] decodedTokenValues = new String(Base64.getDecoder().decode(token)).split(":");

        authenticationRestClient.verifyAccessToken(token);

        request.setAttribute("profileId", decodedTokenValues[2]);
        request.setAttribute("clientId", decodedTokenValues[4]);
        request.setAttribute("token", token);
        return true;

    }

    private String getToken(String authorization) throws InvalidRequestException {
        if (authorization == null) {
            throw new InvalidRequestException("Authorization header is required");
        }

        String[] sessionToken = authorization.split("\\s");
        if (sessionToken.length != 2 || !sessionToken[0].equals("Token")) {
            throw new InvalidRequestException("Authorization header value is wrong, it must contain: Token <token>");
        }
        return sessionToken[1];
    }
}