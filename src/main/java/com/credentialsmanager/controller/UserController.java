package com.credentialsmanager.controller;

import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.credentialsmanager.constants.UrlConstants.*;

@RestController
@Tag(name = "User")
@RequestMapping(BASE_PATH_USER)
public interface UserController {

    @PatchMapping(CHANGE_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> changeEmail(@Parameter(description = "Dto to change email")
                                       @Valid @RequestBody ChangeEmailDto changeEmailDto,
                                       HttpServletRequest request);

    @PutMapping(CHANGE_INFORMATION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> changeInformation(@Parameter(description = "Dto to change information")
                                             @Valid @RequestBody ChangeInformationDto changeInformationDto,
                                             HttpServletRequest request);
}
