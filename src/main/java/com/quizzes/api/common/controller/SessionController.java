package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.service.SessionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/quizzes/api")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @ApiOperation(
            value = "Get Authorization",
            notes = "Generates a session token for a user of a valid Client (LMS). Basically this endpoint " +
                    "assumes that the user information sent by the Client corresponds to an authenticated user " +
                    "in the Client’s web application. The endpoint will verify that the provided API key and API " +
                    "secret are correct, if so then a session token will be returned, otherwise an error will be " +
                    "returned.\n" +
                    "If the session token has not expired the endpoint will return the same session token, " +
                    "otherwise a new session will be generated and returned.\n" +
                    "If the provided user does not exist in Quizzes, then we will create a new Profile in " +
                    "Quizzes corresponding to this Client’s user. If the user already exist then we do not " +
                    "do anything with it.\n" +
                    "The session token generated will correspond only to the user passed in the request body.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns a session token", response = SessionTokenDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/session/authorization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionTokenDto> authorize(
            @ApiParam(value = "Json body", required = true, name = "Body")
            @RequestBody SessionPostRequestDto sessionPostRequestDto) {

        SessionTokenDto session = sessionService.generateToken(sessionPostRequestDto);
        return new ResponseEntity<>(session, HttpStatus.OK);
    }

}

