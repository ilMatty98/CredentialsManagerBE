package com.credentialsmanager.service;

import java.util.Map;

public interface TokenJwtService {

    String generateTokenJwt(long tokenExpiration, String subjetc, Map<String, Object> claims);

    boolean verifySignAndRole(String token, String role);

}
