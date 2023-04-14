package com.credentialsmanager.controller;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication")
@RequestMapping("/v1/authentication")
public interface AuthenticationController {

    @PostMapping("/signUp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SignUp successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> signUp(@Parameter(description = "Dto to create a new user")
                                  @Valid @RequestBody RegistrationDto registrationDto);

    @PostMapping("/logIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    LoginDto.Response logIn(@Parameter(description = "Dto to log in")
                            @Valid @RequestBody LoginDto.Request requestLoginDto);
}
