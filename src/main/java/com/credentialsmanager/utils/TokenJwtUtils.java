package com.credentialsmanager.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

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

@UtilityClass
public class TokenJwtUtils {

    private static final String ALGORITHM = "RSA";

    @SneakyThrows
    public static String generateTokenJwt(String privateKey, long tokenExpiration, String subjetc, Map<String, Object> claims) {
        var now = Instant.now();
        return Jwts.builder()
                .setSubject(subjetc)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(getPrivateKey(privateKey), SignatureAlgorithm.PS512)
                .compact();
    }

    public static boolean verifyToken(String token, String publicKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getPublicKey(publicKey))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @SneakyThrows
    private PrivateKey getPrivateKey(String privateKeyBase64) {
        var privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        var privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    private PublicKey getPublicKey(String publicKeyBase64) {
        var publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        var publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(publicKeySpec);
    }
}
