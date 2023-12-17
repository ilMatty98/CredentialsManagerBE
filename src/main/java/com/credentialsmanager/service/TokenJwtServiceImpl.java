package com.credentialsmanager.service;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Data
@Slf4j
@Component
public class TokenJwtServiceImpl implements TokenJwtService {

    @Value("${token.expiration-minutes}")
    private long tokenExpiration;

    private static RSAPublicKey publicKey;
    private static RSAPrivateKey privateKey;

    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    @PostConstruct
    private void init() {
        generateKeyPair();
    }

    @Scheduled(cron = "${token.key-rotation.cron}")
    private static void generateKeyPair() {
        try {
            log.info("Started generation of key pair for jwt token");
            Security.addProvider(new BouncyCastleProvider());
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(KEY_SIZE);

            var keyPair = keyPairGenerator.generateKeyPair();
            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
            log.info("Finished generating key pair for jwt token");
        } catch (Exception e) {
            log.error("Error creating keys", e);
        }
    }

    @Override
    @SneakyThrows
    public String generateTokenJwt(String subjetc, Map<String, Object> claims) {
        var now = Instant.now();
        return Jwts.builder()
                .setSubject(subjetc)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(tokenExpiration, ChronoUnit.MINUTES)))
                .signWith(privateKey, SignatureAlgorithm.PS512)
                .compact();
    }

    @Override
    public Claims getBody(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return Jwts.claims();
        }
    }

    @Override
    @SneakyThrows
    public String getPublicKey() {
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        var keySpec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
        var encodedKey = keySpec.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    @Override
    public String getEmailFromToken(HttpServletRequest request) {
        var token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null && token.startsWith(AUTH_HEADER_PREFIX)) {
            token = token.substring(7);
            var claims = getBody(token);
            if (claims != null && !claims.isEmpty() &&
                    UserStateEnum.VERIFIED.name().equals(claims.get(TokenClaimEnum.ROLE.getLabel()))) {
                request.setAttribute(TokenClaimEnum.CLAIMS.getLabel(), claims);
                return Optional.of(claims)
                        .map(c -> c.get(TokenClaimEnum.EMAIL.getLabel()))
                        .map(Object::toString)
                        .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_08));
            }
        }
        throw new UnauthorizedException(MessageEnum.ERROR_08);
    }
}
