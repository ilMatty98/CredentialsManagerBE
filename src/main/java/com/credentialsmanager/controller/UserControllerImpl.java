package com.credentialsmanager.controller;

import com.credentialsmanager.dto.request.ChangeEmailDto;
import com.credentialsmanager.dto.request.ChangeInformationDto;
import com.credentialsmanager.service.TokenJwtService;
import com.credentialsmanager.service.UserService;
import com.credentialsmanager.utils.ControllerUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final TokenJwtService tokenJwtService;
    private final UserService userService;

    @Override
    public ResponseEntity<Object> changeEmail(ChangeEmailDto changeEmailDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            userService.changeEmail(changeEmailDto, tokenJwtService.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeInformation(ChangeInformationDto changeInformationDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            userService.changeInformation(changeInformationDto, tokenJwtService.getEmailFromToken(request));
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }
}
