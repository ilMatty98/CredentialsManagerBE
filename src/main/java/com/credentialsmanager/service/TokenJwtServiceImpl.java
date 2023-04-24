package com.credentialsmanager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class TokenJwtServiceImpl implements TokenJwtService {

    @Value("${token.public-key}")
    private String tokenPublicKey;

    @Value("${token.private-key}")
    private String tokenPrivateKey;

    private static final String ALGORITHM = "RSA";

    @Override
    @SneakyThrows
    public String generateTokenJwt(long tokenExpiration, String subjetc, Map<String, Object> claims) {
        var now = Instant.now();
        return Jwts.builder()
                .setSubject(subjetc)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(getPrivateKey(tokenPrivateKey), SignatureAlgorithm.PS512)
                .compact();
    }

    @Override
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getPublicKey(tokenPublicKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return Jwts.claims();
        }
    }


    @SneakyThrows
    private static PrivateKey getPrivateKey(String privateKeyBase64) {
        var privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        var privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    private static PublicKey getPublicKey(String publicKeyBase64) {
        var publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        var publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(publicKeySpec);
    }
}
