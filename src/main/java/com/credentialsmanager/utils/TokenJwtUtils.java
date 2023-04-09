package com.credentialsmanager.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@UtilityClass
public class TokenJwtUtils {

    @SneakyThrows
    public static String generateTokenJwt(String tokenKeyPart1, SecretKey tokenKeyPart2, long tokenExpiration,
                                          Map<String, Object> claims) {
        var byteArray = new ByteArrayOutputStream();
        byteArray.write(Base64.getDecoder().decode(tokenKeyPart1));
        byteArray.write(tokenKeyPart2.getEncoded());

        var hmacKey = new SecretKeySpec(byteArray.toByteArray(), SignatureAlgorithm.HS512.getJcaName());
        var now = Instant.now();

        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
    }

    public static boolean validateTokenJwt(String jwtString, String key) {
        var hmacKey = new SecretKeySpec(Base64.getDecoder().decode(key), SignatureAlgorithm.HS512.getJcaName());

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

    public String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
}
