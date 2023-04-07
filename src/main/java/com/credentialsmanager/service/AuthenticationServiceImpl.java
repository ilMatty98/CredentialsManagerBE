package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UsersRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
import com.credentialsmanager.utils.MessageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private final UsersRepository usersRepository;

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

        usersRepository.save(authenticationMapper.dtotoUser(authenticationDto, salt, hash));

        return authenticationDto;
    }
}
