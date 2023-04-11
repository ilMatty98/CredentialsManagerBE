package com.credentialsmanager.controller;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.dto.TokenJwtDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/authentication")
@Tag(name = "Authentication")
@CrossOrigin("*")
public interface AuthenticationController {

    @PostMapping("/signUp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SignUp successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    AuthenticationDto signUp(@Parameter(description = "Dto to create a new user")
                             @Valid @RequestBody AuthenticationDto authenticationDto);

    @PostMapping("/logIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    TokenJwtDto logIn(@Parameter(description = "Dto to log in")
                            @Valid @RequestBody AuthenticationDto authenticationDto);

    @GetMapping("/validateJwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully")})
    @Deprecated(since = "Only for test")
    boolean validateJwt(@Parameter(description = "Dto to validate token jwt")
                      @Valid @RequestBody TokenJwtDto tokenJwtDto);
}
