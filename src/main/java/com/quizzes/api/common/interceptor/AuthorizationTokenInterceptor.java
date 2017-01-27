package com.quizzes.api.common.interceptor;

import com.quizzes.api.core.exceptions.InvalidSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthorizationTokenInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String token = getToken(authorization);

            Map<String,String> decodedToken = decodeToken(token);
            request.setAttribute("profileId", decodedToken.get("profile-id"));
            request.setAttribute("clientId", decodedToken.get("client-id"));
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

    private Map<String, String> decodeToken(String token) throws InvalidSessionException {
        Map<String,String> result = new HashMap<>();
        try {
            byte[] decodedToken = Base64.getDecoder().decode(token);
            String utf8Token = new String(decodedToken, "utf-8");
            String[] splittedToken = utf8Token.split(":");
            result.put("timestamp", splittedToken[0]);
            result.put("profile-id", splittedToken[1]);
            result.put("client-id", splittedToken[2]);
        }
        catch (UnsupportedEncodingException uee) {
            throw new InvalidSessionException("Wrong Authorization Token " + token);
        }
        catch (IllegalArgumentException iae) {
            throw new InvalidSessionException("Wrong Authorization Token " + token);
        }
        return result;
    }
}