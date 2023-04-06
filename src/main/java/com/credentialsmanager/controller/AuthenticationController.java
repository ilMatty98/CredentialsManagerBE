package com.credentialsmanager.controller;

import com.credentialsmanager.dto.AuthenticationDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/v1/authentication")
@Tag(name = "Authentication")
public interface AuthenticationController {

    @PostMapping("/signIn")
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Dto not correct")})
    AuthenticationDto signIn(@Parameter(description = "Dto to create a new user")
                             @Valid @RequestBody AuthenticationDto authenticationDto) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
