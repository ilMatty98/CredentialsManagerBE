package com.credentialsmanager.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@UtilityClass
public class TokenJwtUtils {

    @SneakyThrows
    public static String generateTokenJwt(String tokenKeyPart1, SecretKey tokenKeyPart2, long tokenExpiration,
                                          String subjetc, Map<String, Object> claims) {
        var byteArray = appendTwoByteArray(base64Decoding(tokenKeyPart1), tokenKeyPart2.getEncoded());

        var hmacKey = new SecretKeySpec(byteArray, SignatureAlgorithm.HS512.getJcaName());
        var now = Instant.now();

        return Jwts.builder()
                .setSubject(subjetc)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
    }

    public static byte[] appendTwoByteArray(byte[] array1, byte[] array2) throws IOException {
        var byteArray = new ByteArrayOutputStream();
        byteArray.write(array1);
        byteArray.write(array2);
        return byteArray.toByteArray();
    }

    public static boolean validateTokenJwt(String jwtString, String key) {
        var hmacKey = new SecretKeySpec(base64Decoding(key), SignatureAlgorithm.HS512.getJcaName());

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

    public static String getSubject(String jwtString) {
        String subject;
        try {
            subject = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(jwtString)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            subject = null;
        }
        return subject;
    }

    public String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public byte[] base64Decoding(String input) {
        return Base64.getDecoder().decode(input);
    }
}
