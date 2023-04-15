package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailType;
import com.credentialsmanager.constants.MessageUtils;
import com.credentialsmanager.dto.EmailDto;
import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
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

    @Value("${token.public-key}")
    private String tokenPublicKey;

    @Value("${token.private-key}")
    private String tokenPrivateKey;

    private final EmailService emailService;

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

        user.setTimestampLastAccess(getCurrentTimestamp());
        usersRepository.save(user);

        var token = TokenJwtUtils.generateTokenJwt(tokenPrivateKey, tokenExpiration, user.getEmail(), new HashMap<>());

        emailService.sendEmail(new EmailDto(user.getEmail(), user.getLanguage(), EmailType.SING_UP));

        return authenticationMapper.newLoginDto(user, token, tokenPublicKey);
    }

    @Override
    public boolean checkEmail(String email) {
        return usersRepository.existsById(email);
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}
