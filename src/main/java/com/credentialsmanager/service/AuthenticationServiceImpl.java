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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
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

        var tokenKey = TokenJwtUtils.generateSecretKey().getEncoded();

        //TODO: criptare la chiave prima di trasformarla in base64
        user.setTimestampLastAccess(getCurrentTimestamp());
        user.setToken(base64Encoding(tokenKey));
        usersRepository.save(user);

        var token = TokenJwtUtils.generateTokenJwt(tokenKey, tokenExpiration, user.getEmail(), new HashMap<>());
        return new TokenJwtDto(token);
    }

    @Override
    @SneakyThrows
    public boolean validateJwt(TokenJwtDto tokenJwtDto) {
        var email = TokenJwtUtils.getSubject(tokenJwtDto.token());
        if (email == null) return false;
        var user = usersRepository.findById(email)
                .orElseThrow(() -> new UnauthorizedException(MessageUtils.ERROR_03.getMessage()));

        return TokenJwtUtils.validateTokenJwt(tokenJwtDto.token(), base64Decoding(user.getToken()));
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }

    private static String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    private static byte[] base64Decoding(String input) {
        return Base64.getDecoder().decode(input);
    }
}
