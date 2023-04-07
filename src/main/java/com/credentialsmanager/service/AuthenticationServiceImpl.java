package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
import com.credentialsmanager.utils.MessageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

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
    public AuthenticationDto logIn(AuthenticationDto authenticationDto) {
        var user = usersRepository.findById(authenticationDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageUtils.ERROR_02.getMessage()));

        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currenthash = AuthenticationUtils.generateArgon2id(authenticationDto.getPassword(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currenthash))
            throw new UnauthorizedException(MessageUtils.ERROR_02.getMessage());

        user.setTimestampLastAccess(getCurrentTimestamp());
        usersRepository.save(user);

        return new AuthenticationDto();
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}
