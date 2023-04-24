package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.exception.NotFoundException;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${fe.endpoint}")
    private String endpointFe;

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

    private final EmailService emailService;

    private final TokenJwtService tokenJwtService;

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

        var dynamicLabels = Collections.singletonMap("href", endpointFe + user.getEmail() + "/" + user.getVerificationCode() + "/confirm");

        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.SING_UP, dynamicLabels);
        usersRepository.save(user);
    }

    @Override
    @SneakyThrows
    public LogInDto.Response logIn(LogInDto.Request requestLogInDto) {
        var user = usersRepository.findByEmail(requestLogInDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_02));

        if (UserStateEnum.UNVERIFIED.equals(user.getState()))
            throw new UnauthorizedException(MessageEnum.ERROR_06);

        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currentHash = AuthenticationUtils.generateArgon2id(requestLogInDto.getMasterPasswordHash(), salt,
                argon2idSize, argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currentHash))
            throw new UnauthorizedException(MessageEnum.ERROR_02);

        user.setTimestampLastAccess(getCurrentTimestamp());
        usersRepository.save(user);

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.EMAIL.getLabel(), user.getEmail());
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState().name());

        var token = tokenJwtService.generateTokenJwt(tokenExpiration, user.getEmail(), claims);

        var dynamicLabels = Map.ofEntries(
                entry("date_value", requestLogInDto.getLocalDateTime()),
                entry("ipAddress_value", requestLogInDto.getIpAddress()),
                entry("device_value", requestLogInDto.getDeviceType())
        );

        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.LOG_IN, dynamicLabels);
        return authenticationMapper.newLoginDto(user, token, tokenPublicKey);
    }

    @Override
    public boolean checkEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    public void confirmEmail(String email, String code) {
        var user = usersRepository.findByEmailAndVerificationCode(email, code)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_05));

        user.setState(UserStateEnum.VERIFIED);
        user.setVerificationCode(null);
        usersRepository.save(user);
    }

    @Override
    public void changePassword(SignUpDto signUpDto) {

    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}
