package com.credentialsmanager.controller;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
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
    public void signUp(RegistrationDto registrationDto) {
        try {
            authenticationService.signUp(registrationDto);
            //TODO far tornare un 201
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public LoginDto.Response logIn(LoginDto.Request requestLoginDto) {
        try {
            return authenticationService.logIn(requestLoginDto);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public boolean validateJwt(String tokenJwt) {
        try {
            return authenticationService.validateJwt(tokenJwt);
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }
}
