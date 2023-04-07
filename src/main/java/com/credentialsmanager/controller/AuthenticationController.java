package com.credentialsmanager.controller;

import com.credentialsmanager.dto.AuthenticationDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/authentication")
@Tag(name = "Authentication")
public interface AuthenticationController {

    @PostMapping("/signIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SignIn successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    AuthenticationDto signIn(@Parameter(description = "Dto to create a new user")
                             @Valid @RequestBody AuthenticationDto authenticationDto);

    @GetMapping("/logIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    AuthenticationDto logIn(@Parameter(description = "Dto to log in")
                            @Valid @RequestBody AuthenticationDto authenticationDto);
}
