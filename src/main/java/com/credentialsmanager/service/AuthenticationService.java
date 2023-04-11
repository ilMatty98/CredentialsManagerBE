package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.dto.TokenJwtDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    AuthenticationDto signUp(AuthenticationDto authenticationDto);

    TokenJwtDto logIn(AuthenticationDto authenticationDto);

    boolean validateJwt(TokenJwtDto tokenJwtDto);
}
