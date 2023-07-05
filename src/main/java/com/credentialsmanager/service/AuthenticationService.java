package com.credentialsmanager.service;

import com.credentialsmanager.dto.request.*;
import com.credentialsmanager.dto.response.AccessDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    void signUp(SignUpDto signUpDto);

    AccessDto logIn(LogInDto logInDto);

    boolean checkEmail(String email);

    void confirmEmail(String email, String code);

    void changePassword(ChangePasswordDto changePasswordDto, String email);

    void sendHint(String email);

    void deleteAccount(String email);

    void changeEmail(ChangeEmailDto changeEmailDto, String oldEmail);

    void confirmChangeEmail(ConfirmChangeEmailDto confirmChangeEmailDto, String oldEmail);

    void changeInformation(ChangeInformationDto changeInformationDto, String email);
}
