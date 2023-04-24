package com.credentialsmanager.service;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface TokenJwtService {

    String generateTokenJwt(long tokenExpiration, String subjetc, Map<String, Object> claims);

    Claims getClaims(String token);

}
