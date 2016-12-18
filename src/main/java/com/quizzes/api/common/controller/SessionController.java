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
            value = "Authorize session",
            notes = "Generates a valid session token")
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

