package com.credentialsmanager.controller;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    @Override
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        return authenticationService.signIn(authenticationDto);
    }
}
