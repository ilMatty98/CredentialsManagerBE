package com.credentialsmanager.controller;

import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
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

    private final UserService userService;

    @Override
    public ResponseEntity<Object> changeEmail(ChangeEmailDto changeEmailDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            changeEmailDto.setEmail(ControllerUtils.getEmailFromToken(request));
            userService.changeEmail(changeEmailDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeInformation(ChangeInformationDto changeInformationDto, HttpServletRequest request) {
        return ControllerUtils.handleRequest(() -> {
            changeInformationDto.setEmail(ControllerUtils.getEmailFromToken(request));
            userService.changeInformation(changeInformationDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }
}
