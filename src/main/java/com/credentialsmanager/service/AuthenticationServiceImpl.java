package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.*;
import com.credentialsmanager.dto.response.AccessDto;
import com.credentialsmanager.entity.User;
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

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Value("${change-email.expiration-minutes}")
    private int emailChangeExpirationMn;

    @Value("${change-email.attempts}")
    private int emailChangeAttempts;

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
    public AccessDto logIn(LogInDto logInDto) {
        var user = usersRepository.findByEmail(logInDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_02));

        if (UserStateEnum.UNVERIFIED.equals(user.getState()))
            throw new UnauthorizedException(MessageEnum.ERROR_04);

        checkPassword(user, logInDto.getMasterPasswordHash());

        user.setTimestampLastAccess(getCurrentTimestamp());
        usersRepository.save(user);

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.EMAIL.getLabel(), user.getEmail());
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState().name());

        var token = tokenJwtService.generateTokenJwt(user.getEmail(), claims);

        var dynamicLabels = Map.ofEntries(
                entry("date_value", logInDto.getLocalDateTime()),
                entry("ipAddress_value", logInDto.getIpAddress()),
                entry("device_value", logInDto.getDeviceType())
        );

        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.LOG_IN, dynamicLabels);
        return authenticationMapper.newAccessDto(user, token, tokenJwtService.getPublicKey());
    }

    @Override
    public boolean checkEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void confirmEmail(String email, String code) {
        var user = usersRepository.findByEmailAndVerificationCode(email, code)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        user.setState(UserStateEnum.VERIFIED);
        user.setVerificationCode(null);
        usersRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto, String email) {
        var user = usersRepository.findByEmailAndState(email, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        checkPassword(user, changePasswordDto.getCurrentMasterPasswordHash());

        var salt = AuthenticationUtils.generateSalt(saltSize);
        var hash = AuthenticationUtils.generateArgon2id(changePasswordDto.getNewMasterPasswordHash(), salt, argon2idSize,
                argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        user.setTimestampPassword(getCurrentTimestamp());
        user.setSalt(authenticationMapper.base64Encoding(salt));
        user.setHash(authenticationMapper.base64Encoding(hash));
        user.setInitializationVector(authenticationMapper.base64EncodingString(changePasswordDto.getNewInitializationVector()));
        user.setProtectedSymmetricKey(authenticationMapper.base64EncodingString(changePasswordDto.getNewProtectedSymmetricKey()));

        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.CHANGE_PSW, new HashMap<>());
        usersRepository.save(user);
    }

    @Override
    public void sendHint(String email) {
        var user = usersRepository.findByEmailAndState(email, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        var dynamicLabels = Map.ofEntries(entry("hint_value", user.getHint()));
        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.SEND_HINT, dynamicLabels);
    }

    @Override
    @Transactional
    public void deleteAccount(String email, DeleteDto deleteDto) {
        var user = usersRepository.findByEmailAndState(email, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        checkPassword(user, deleteDto.getMasterPasswordHash());

        usersRepository.delete(user);
        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.DELETE_USER, new HashMap<>());
    }

    @Override
    @Transactional
    public void changeEmail(ChangeEmailDto changeEmailDto, String oldEmail) {
        if (oldEmail.equals(changeEmailDto.getEmail()) || usersRepository.existsByEmail(changeEmailDto.getEmail()))
            throw new BadRequestException(MessageEnum.ERROR_01);

        var user = usersRepository.findByEmailAndState(oldEmail, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        checkPassword(user, changeEmailDto.getMasterPasswordHash());

        user.setTimestampEmail(getCurrentTimestamp());
        user.setVerificationCode(generateVerificationCode());
        user.setNewEmail(changeEmailDto.getEmail());
        user.setAttempt(0);

        var dynamicLabels = Map.ofEntries(entry("email", changeEmailDto.getEmail()));
        emailService.sendEmail(oldEmail, user.getLanguage(), EmailTypeEnum.CHANGE_EMAIL_NOTIFICATION, dynamicLabels);

        dynamicLabels = Map.ofEntries(entry("code", user.getVerificationCode()));
        emailService.sendEmail(changeEmailDto.getEmail(), user.getLanguage(), EmailTypeEnum.CHANGE_EMAIL_CODE, dynamicLabels);
        usersRepository.save(user);
    }

    @Override
    @Transactional(dontRollbackOn = BadRequestException.class)
    public void confirmChangeEmail(ConfirmChangeEmailDto confirmChangeEmailDto, String oldEmail) {
        if (usersRepository.existsByEmail(confirmChangeEmailDto.getEmail()))
            throw new BadRequestException(MessageEnum.ERROR_01);

        var user = usersRepository.findByEmailAndNewEmailAndState(oldEmail, confirmChangeEmailDto.getEmail(), UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        checkPassword(user, confirmChangeEmailDto.getMasterPasswordHash());

        MessageEnum messageError = null;
        var currentTime = LocalDateTime.now();
        var maximumTime = user.getTimestampEmail().toLocalDateTime().plusMinutes(emailChangeExpirationMn);

        if (currentTime.isAfter(maximumTime)) { //Time out
            messageError = MessageEnum.ERROR_06;
        } else if (user.getAttempt() >= emailChangeAttempts) {//The attempt limit has been reached
            messageError = MessageEnum.ERROR_05;
        } else if (!confirmChangeEmailDto.getVerificationCode().equals(user.getVerificationCode())) { //Incorrect verification code
            user.setAttempt(user.getAttempt() + 1);
            usersRepository.save(user);
            throw new BadRequestException(MessageEnum.ERROR_07);
        } else {    //Ok
            var salt = AuthenticationUtils.generateSalt(saltSize);
            var hash = AuthenticationUtils.generateArgon2id(confirmChangeEmailDto.getNewMasterPasswordHash(), salt,
                    argon2idSize, argon2idIterations, argon2idMemoryKB, argon2idParallelism);

            user.setEmail(user.getNewEmail());
            user.setSalt(authenticationMapper.base64Encoding(salt));
            user.setHash(authenticationMapper.base64Encoding(hash));
            user.setInitializationVector(authenticationMapper.base64EncodingString(confirmChangeEmailDto.getNewInitializationVector()));
            user.setProtectedSymmetricKey(authenticationMapper.base64EncodingString(confirmChangeEmailDto.getNewProtectedSymmetricKey()));
            emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.CHANGE_EMAIL, new HashMap<>());
        }

        user.setVerificationCode(null);
        user.setNewEmail(null);
        user.setAttempt(null);
        usersRepository.save(user);

        if (messageError != null) throw new BadRequestException(messageError);
    }

    @Override
    @Transactional
    public void changeInformation(ChangeInformationDto changeInformationDto, String email) {
        var user = usersRepository.findByEmailAndState(email, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_03));

        user.setHint(changeInformationDto.getHint());
        user.setLanguage(changeInformationDto.getLanguage());
        user.setPropic(changeInformationDto.getPropic());
        usersRepository.save(user);
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }

    private void checkPassword(User user, String masterPasswordHash) {
        var storedHash = Base64.getDecoder().decode(user.getHash());
        var salt = Base64.getDecoder().decode(user.getSalt());
        var currentHash = AuthenticationUtils.generateArgon2id(masterPasswordHash, salt,
                argon2idSize, argon2idIterations, argon2idMemoryKB, argon2idParallelism);

        if (!Arrays.equals(storedHash, currentHash)) throw new UnauthorizedException(MessageEnum.ERROR_02);
    }

    private static String generateVerificationCode() {
        var secureRandom = new SecureRandom();
        return IntStream.range(0, 6)
                .mapToObj(i -> String.valueOf(secureRandom.nextInt(10)))
                .collect(Collectors.joining());
    }
}
