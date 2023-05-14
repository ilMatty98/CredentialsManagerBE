package com.credentialsmanager.controller;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.dto.ChangeEmailDto;
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
    public LogInDto.Response logIn(LogInDto.Request requestLogInDto) {
        try {
            return authenticationService.logIn(requestLogInDto);
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
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public ResponseEntity<Object> changePassword(SignUpDto signUpDto, HttpServletRequest request) {
        try {
            signUpDto.setEmail(getEmailFromToken(request));
            authenticationService.changePassword(signUpDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public ResponseEntity<Object> changeEmail(ChangeEmailDto changeEmailDto, HttpServletRequest request) {
        try {
            changeEmailDto.setCurrentEmail(getEmailFromToken(request));
            authenticationService.changeEmail(changeEmailDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public ResponseEntity<Object> changeLanguage(String language, HttpServletRequest request) {
        try {
//            authenticationService.changeLanguage(getEmail(request), language);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    @Override
    public ResponseEntity<Object> changeHint(String hint, HttpServletRequest request) {
        try {
//            authenticationService.changeHint(getEmail(request), hint);
            return ResponseEntity.status(HttpStatus.OK).build();
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
