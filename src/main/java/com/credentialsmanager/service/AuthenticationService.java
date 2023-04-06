package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public interface AuthenticationService {

    AuthenticationDto signIn(AuthenticationDto authenticationDto) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
