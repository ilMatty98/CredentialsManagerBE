package com.credentialsmanager.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@UtilityClass
public class TokenJwtUtils {

    @SneakyThrows
    public static String generateTokenJwt(byte[] key, long tokenExpiration, String subjetc, Map<String, Object> claims) {
        var hmacKey = new SecretKeySpec(key, SignatureAlgorithm.HS512.getJcaName());
        var now = Instant.now();

        return Jwts.builder()
                .setSubject(subjetc)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
    }

    public static boolean validateTokenJwt(String jwtString, byte[] key) {
        var hmacKey = new SecretKeySpec(key, SignatureAlgorithm.HS512.getJcaName());

        try {
            Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(jwtString);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    @SuppressWarnings("java:S1874")
    public static String getSubject(String jwtString) {
        var withoutSignature = jwtString.substring(0, jwtString.lastIndexOf('.') + 1);
        return Jwts.parser().parseClaimsJwt(withoutSignature).getBody().getSubject();
    }

    public SecretKey generateSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}
