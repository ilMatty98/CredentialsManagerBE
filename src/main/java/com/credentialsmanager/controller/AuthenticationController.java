package com.credentialsmanager.controller;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<Object> signUp(@Parameter(description = "Dto to create a new user")
                                  @Valid @RequestBody RegistrationDto registrationDto);

    @PostMapping("/logIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    LoginDto.Response logIn(@Parameter(description = "Dto to log in")
                            @Valid @RequestBody LoginDto.Request requestLoginDto);

    @GetMapping("/validateJwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully")})
    @Deprecated(since = "Only for test")
    boolean validateJwt(@Parameter(description = "Token jwt")
                        @Valid @RequestBody String tokenJwt);
}
