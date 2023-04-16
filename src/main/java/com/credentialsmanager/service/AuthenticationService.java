package com.credentialsmanager.service;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.SignUpDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    void signUp(SignUpDto signUpDto);

    LoginDto.Response logIn(LoginDto.Request requestLoginDto);

    boolean checkEmail(String email);

    void confirmEmail(String email, String code);
}
