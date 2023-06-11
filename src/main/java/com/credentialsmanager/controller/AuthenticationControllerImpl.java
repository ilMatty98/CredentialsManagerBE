package com.credentialsmanager.controller;

import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.service.AuthenticationService;
import com.credentialsmanager.utils.ControllerUtils;
import jakarta.servlet.http.HttpServletRequest;
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
        return ControllerUtils.handleRequest(() -> {
            authenticationService.signUp(signUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }

    @Override
    public LogInDto.Response logIn(LogInDto.Request requestLogInDto) {
        return ControllerUtils.handleRequest(() -> authenticationService.logIn(requestLogInDto));
    }

    @Override
    public boolean checkEmail(String email) {
        return ControllerUtils.handleRequest(() -> authenticationService.checkEmail(email));
    }

    @Override
    public ResponseEntity<Object> confirmEmail(String email, String code) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.confirmEmail(email, code);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changePassword(SignUpDto signUpDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            signUpDto.setEmail(ControllerUtils.getEmailFromToken(request));
            authenticationService.changePassword(signUpDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> sendHint(String email) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.sendHint(email);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> deleteAccount(HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.deleteAccount(ControllerUtils.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

}
