package com.credentialsmanager.controller;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.exception.CustomException;
import com.credentialsmanager.exception.GenericErrorException;
import com.credentialsmanager.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<Object> signUp(SignUpDto signUpDto) {
        try {
            authenticationService.signUp(signUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public boolean checkEmail(String email) {
        try {
            return authenticationService.checkEmail(email);
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public ResponseEntity<Object> confirmEmail(String email, String code) {
        try {
            authenticationService.confirmEmail(email, code);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }
}
