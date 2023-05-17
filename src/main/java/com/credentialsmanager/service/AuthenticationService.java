package com.credentialsmanager.service;

import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    void signUp(SignUpDto signUpDto);

    LogInDto.Response logIn(LogInDto.Request requestLogInDto);

    boolean checkEmail(String email);

    void confirmEmail(String email, String code);

    void changePassword(SignUpDto signUpDto);

    void changeEmail(ChangeEmailDto changeEmailDto);

    void changeInformation(ChangeInformationDto changeInformationDto);

    void sendHint(String email);

    void deleteAccount(String email);
}
