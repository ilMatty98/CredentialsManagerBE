package com.credentialsmanager.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface TokenJwtService {

    String generateTokenJwt(String subjetc, Map<String, Object> claims);

    Claims getBody(String token);

    String getPublicKey();

    String getEmailFromToken(HttpServletRequest request);
}
