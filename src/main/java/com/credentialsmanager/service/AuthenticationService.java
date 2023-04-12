package com.credentialsmanager.service;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    void signUp(RegistrationDto registrationDto);

    LoginDto.Response logIn(LoginDto.Request requestLoginDto);

    boolean validateJwt(String tokenJwt);
}
