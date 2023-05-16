package com.credentialsmanager.service;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface TokenJwtService {

    String generateTokenJwt(String subjetc, Map<String, Object> claims);

    Claims getBody(String token);

    String getPublicKey();

}
