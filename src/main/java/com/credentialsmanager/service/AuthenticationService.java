package com.credentialsmanager.service;

import com.credentialsmanager.dto.request.LogInDto;
import com.credentialsmanager.dto.request.SignUpDto;
import com.credentialsmanager.dto.response.AccessDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    void signUp(SignUpDto signUpDto);

    AccessDto logIn(LogInDto logInDto);

    boolean checkEmail(String email);

    void confirmEmail(String email, String code);

    void changePassword(SignUpDto signUpDto);

    void sendHint(String email);

    void deleteAccount(String email);
}
