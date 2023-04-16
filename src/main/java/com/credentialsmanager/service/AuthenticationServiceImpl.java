package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.SignUpDto;
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
    public void signUp(SignUpDto signUpDto) {
        if (usersRepository.existsByEmail(signUpDto.getEmail()))
            throw new BadRequestException(MessageEnum.ERROR_01);

        var salt = AuthenticationUtils.generateSalt(saltSize);
        var hash = AuthenticationUtils.generateArgon2id(signUpDto.getMasterPasswordHash(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        var user = authenticationMapper.newUser(signUpDto, salt, hash, getCurrentTimestamp(), UserStateEnum.UNVERIFIED);
        usersRepository.save(user);
    }

    @Override
    @SneakyThrows
    public LoginDto.Response logIn(LoginDto.Request requestLoginDto) {
        var user = usersRepository.findByEmailAndStateIs(requestLoginDto.getEmail(), UserStateEnum.VERIFIED)
                .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_02));

        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currentHash = AuthenticationUtils.generateArgon2id(requestLoginDto.getMasterPasswordHash(), salt,
                argon2idSize, argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currentHash))
            throw new UnauthorizedException(MessageEnum.ERROR_02);

        user.setTimestampLastAccess(getCurrentTimestamp());
        usersRepository.save(user);

        var token = TokenJwtUtils.generateTokenJwt(tokenPrivateKey, tokenExpiration, user.getEmail(), new HashMap<>());

        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.SING_UP);

        return authenticationMapper.newLoginDto(user, token, tokenPublicKey);
    }

    @Override
    public boolean checkEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}
