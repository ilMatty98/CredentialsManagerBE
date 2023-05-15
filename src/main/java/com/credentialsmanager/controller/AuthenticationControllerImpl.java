package com.credentialsmanager.controller;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.exception.CustomException;
import com.credentialsmanager.exception.GenericErrorException;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<Object> signUp(SignUpDto signUpDto) {
        return handleRequest(() -> {
            authenticationService.signUp(signUpDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }

    @Override
    public LogInDto.Response logIn(LogInDto.Request requestLogInDto) {
        return handleRequest(() -> authenticationService.logIn(requestLogInDto));
    }

    @Override
    public boolean checkEmail(String email) {
        return handleRequest(() -> authenticationService.checkEmail(email));
    }

    @Override
    public ResponseEntity<Object> confirmEmail(String email, String code) {
        return handleRequest(() -> {
            authenticationService.confirmEmail(email, code);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changePassword(SignUpDto signUpDto, HttpServletRequest request) {
        return handleRequest(() -> {
            signUpDto.setEmail(getEmailFromToken(request));
            authenticationService.changePassword(signUpDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeEmail(ChangeEmailDto changeEmailDto, HttpServletRequest request) {
        return handleRequest(() -> {
            changeEmailDto.setEmail(getEmailFromToken(request));
            authenticationService.changeEmail(changeEmailDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    @Override
    public ResponseEntity<Object> changeInformation(ChangeInformationDto changeInformationDto, HttpServletRequest request) {
        return handleRequest(() -> {
            changeInformationDto.setEmail(getEmailFromToken(request));
            authenticationService.changeInformation(changeInformationDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }

    private <T> T handleRequest(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    private static String getEmailFromToken(HttpServletRequest request) {
        var claims = (Claims) request.getAttribute(TokenClaimEnum.CLAIMS.getLabel());
        return Optional.ofNullable(claims)
                .map(c -> c.get(TokenClaimEnum.EMAIL.getLabel()))
                .map(Object::toString)
                .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_02));
    }
}
