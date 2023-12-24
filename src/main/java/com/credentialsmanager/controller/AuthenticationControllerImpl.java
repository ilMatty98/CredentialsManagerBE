package com.credentialsmanager.controller;

import com.credentialsmanager.dto.request.*;
import com.credentialsmanager.dto.response.AccessDto;
import com.credentialsmanager.service.AuthenticationService;
import com.credentialsmanager.service.TokenJwtService;
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
    private final TokenJwtService tokenJwtService;

    @Override
    public ResponseEntity<Object> signUp(SignUpDto signUpDto) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.signUp(signUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }

    @Override
    public AccessDto logIn(LogInDto logInDto) {
        return ControllerUtils.handleRequest(() -> authenticationService.logIn(logInDto));
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
    public ResponseEntity<Object> changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.changePassword(changePasswordDto, tokenJwtService.getEmailFromToken(request));
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
    public ResponseEntity<Object> deleteAccount(DeleteDto deleteDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.deleteAccount(tokenJwtService.getEmailFromToken(request), deleteDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeEmail(ChangeEmailDto changeEmailDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.changeEmail(changeEmailDto, tokenJwtService.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> confirmChangeEmail(ConfirmChangeEmailDto confirmChangeEmailDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.confirmChangeEmail(confirmChangeEmailDto, tokenJwtService.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeInformation(ChangeInformationDto changeInformationDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            authenticationService.changeInformation(changeInformationDto, tokenJwtService.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

}
