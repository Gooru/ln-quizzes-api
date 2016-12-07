package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.service.ProfileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController("ProfileController")
@RequestMapping("/quizzes/api")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @ApiOperation(
            value = "Find Profile by External ID",
            notes = "This is a temporal endpoint to retrieve Profile data by its External ID and Client ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the Profile ID", response = IdResponseDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/profile-by-external-id/{externalId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDto> getProfileIdByExternalId(
            @PathVariable String externalId,
            @RequestHeader(value = "client-id", defaultValue = "quizzes") String lmsId) {
        IdResponseDto result = profileService.findIdByExternalIdAndLmsId(externalId, Lms.valueOf(lmsId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Find Profile by ID",
            notes = "Retrieves the Profile data for a specified Profile ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the profile data", response = ProfileDto.class),
            @ApiResponse(code = 500, message = "Bad request")})
    @RequestMapping(path = "/v1/profile/{profileId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDto> getProfileById(
            @PathVariable UUID profileId,
            @RequestHeader(value = "client-id", defaultValue = "quizzes") String lmsId) {
        ProfileDto result = profileService.findById(profileId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
