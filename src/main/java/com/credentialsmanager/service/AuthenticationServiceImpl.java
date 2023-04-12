package com.credentialsmanager.service;

import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
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
    public void signUp(RegistrationDto registrationDto) {
        if (usersRepository.existsById(registrationDto.getEmail()))
            throw new BadRequestException(MessageUtils.ERROR_01);

        var salt = AuthenticationUtils.generateSalt(saltSize);
        var hash = AuthenticationUtils.generateArgon2id(registrationDto.getMasterPasswordHash(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        usersRepository.save(authenticationMapper.newUser(registrationDto, salt, hash, getCurrentTimestamp()));
    }

    @Override
    @SneakyThrows
    public LoginDto.Response logIn(LoginDto.Request requestLoginDto) {
        var user = usersRepository.findById(requestLoginDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageUtils.ERROR_02));

        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currentHash = AuthenticationUtils.generateArgon2id(requestLoginDto.getMasterPasswordHash(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currentHash))
            throw new UnauthorizedException(MessageUtils.ERROR_02);

        var tokenKey = TokenJwtUtils.generateSecretKey().getEncoded();

        //TODO: criptare la chiave prima di trasformarla in base64
        user.setTimestampLastAccess(getCurrentTimestamp());
        user.setTokenKey(authenticationMapper.base64Encoding(tokenKey));
        usersRepository.save(user);

        var token = TokenJwtUtils.generateTokenJwt(tokenKey, tokenExpiration, user.getEmail(), new HashMap<>());
        return authenticationMapper.newLoginDto(user, token);
    }

    @Override
    @SneakyThrows
    public boolean validateJwt(String tokenJwt) {
        var email = TokenJwtUtils.getSubject(tokenJwt);
        if (email == null) return false;
        var user = usersRepository.findById(email)
                .orElseThrow(() -> new UnauthorizedException(MessageUtils.ERROR_03));

        return TokenJwtUtils.validateTokenJwt(tokenJwt, authenticationMapper.base64Decoding(user.getTokenKey()));
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}
