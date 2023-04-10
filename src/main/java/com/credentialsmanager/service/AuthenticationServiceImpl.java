package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.dto.TokenJwtDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
import com.credentialsmanager.utils.MessageUtils;
import com.credentialsmanager.utils.TokenJwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${encryption.salt.size}")
    private int saltSize;

    @Value("${encryption.argon2id.size}")
    private int argon2idSize;

    @Value("${encryption.argon2id.iterations}")
    private int argon2idIterations;

    @Value("${encryption.argon2id.memoryKB}")
    private int argon2idMemoryKB;

    @Value("${encryption.argon2id.parallelism}")
    private int argon2idParallelism;

    @Value("${token.key.part1}")
    private String tokenKeyPart1;

    @Value("${token.expiration-minutes}")
    private long tokenExpiration;

    private final UserRepository usersRepository;

    private final AuthenticationMapper authenticationMapper;

    @Override
    @SneakyThrows
    @Transactional
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        if (usersRepository.existsById(authenticationDto.getEmail()))
            throw new BadRequestException(MessageUtils.ERROR_01.getMessage());

        var salt = AuthenticationUtils.generateSalt(saltSize);
        var hash = AuthenticationUtils.generateArgon2id(authenticationDto.getPassword(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        usersRepository.save(authenticationMapper.saveNewUser(authenticationDto, salt, hash, getCurrentTimestamp()));
        return authenticationDto;
    }

    @Override
    @SneakyThrows
    public TokenJwtDto logIn(AuthenticationDto authenticationDto) {
        var user = usersRepository.findById(authenticationDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageUtils.ERROR_02.getMessage()));

        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currenthash = AuthenticationUtils.generateArgon2id(authenticationDto.getPassword(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currenthash))
            throw new UnauthorizedException(MessageUtils.ERROR_02.getMessage());

        var tokenKeyPart2 = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        //TODO: criptare la chiave prima di trasformarla in base64
        user.setTimestampLastAccess(getCurrentTimestamp());
        user.setToken(TokenJwtUtils.base64Encoding(tokenKeyPart2.getEncoded()));
        usersRepository.save(user);

        var token = TokenJwtUtils.generateTokenJwt(tokenKeyPart1, tokenKeyPart2, tokenExpiration, user.getEmail(),
                new HashMap<>());
        return new TokenJwtDto(token);
    }

    @Override
    public boolean validateJwt(TokenJwtDto tokenJwtDto) {
        return false;
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }


    public static void main(String[] args) throws IOException {
        SecretKey chiaveCodice = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        System.out.println(Encoders.BASE64.encode(chiaveCodice.getEncoded()));
        SecretKey chiaveDB = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(chiaveCodice.getEncoded());
        outputStream.write(chiaveDB.getEncoded());

        byte[] chiaveCompleta = outputStream.toByteArray();

        String secretString = Encoders.BASE64.encode(chiaveCompleta);

        System.out.println("--------------------------------------------");
        System.out.println(secretString);


        String jwt = createJwtSignedHMAC(secretString);
        System.out.println("--------------------------------------------");
        System.out.println("TOken: " + jwt);


        Jws<Claims> token = parseJwt(jwt, secretString);

        System.out.println(token.getBody());
    }


    public static Jws<Claims> parseJwt(String jwtString, String secret) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }


    public static String createJwtSignedHMAC(String secret) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS512.getJcaName());

        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
                .claim("name", "Jane Doe")
                .claim("email", "jane@example.com")
                .setSubject("jane")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5L, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();

        return jwtToken;
    }
}
