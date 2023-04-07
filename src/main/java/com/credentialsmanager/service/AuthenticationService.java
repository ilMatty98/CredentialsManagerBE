package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    AuthenticationDto signIn(AuthenticationDto authenticationDto);

    AuthenticationDto logIn(AuthenticationDto authenticationDto);
}
