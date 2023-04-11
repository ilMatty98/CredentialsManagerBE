package com.credentialsmanager.controller;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.dto.TokenJwtDto;
import com.credentialsmanager.exception.CustomException;
import com.credentialsmanager.exception.GenericErrorException;
import com.credentialsmanager.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    @Override
    public AuthenticationDto signUp(AuthenticationDto authenticationDto) {
        try {
            return authenticationService.signUp(authenticationDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public TokenJwtDto logIn(AuthenticationDto authenticationDto) {
        try {
            return authenticationService.logIn(authenticationDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public boolean validateJwt(TokenJwtDto tokenJwtDto) {
        try {
            return authenticationService.validateJwt(tokenJwtDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }
}
