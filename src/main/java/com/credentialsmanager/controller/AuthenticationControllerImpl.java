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
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        AuthenticationDto output;
        try {
            output = authenticationService.signIn(authenticationDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
        return output;
    }

    @Override
    public TokenJwtDto logIn(AuthenticationDto authenticationDto) {
        TokenJwtDto output;
        try {
            output = authenticationService.logIn(authenticationDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
        return output;
    }

    @Override
    public boolean validateJwt(TokenJwtDto tokenJwtDto) {
        return false;
    }
}
