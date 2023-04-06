package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UsersRepository;
import com.credentialsmanager.utils.AuthenticationUtils;
import com.credentialsmanager.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${encryption.salt.size}")
    private int saltSize;

    @Value("${encryption.hash.size}")
    private int hashSize;

    @Value("${encryption.hash.interation}")
    private int hashInterarion;

    @Value("${encryption.hash.algorithm}")
    private String hashAlgorithm;

    private final UsersRepository usersRepository;

    private final AuthenticationMapper authenticationMapper;

    @Override
    @SneakyThrows
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        if (usersRepository.existsById(authenticationDto.getEmail()))
            throw new BadRequestException(MessageUtils.ERROR_01.getMessage());

        var salt = AuthenticationUtils.getSalt(saltSize);
        var hash = AuthenticationUtils.getHash(authenticationDto.getPassword(), salt, hashInterarion, hashSize, hashAlgorithm);

        usersRepository.save(authenticationMapper.dtotoUser(authenticationDto, salt, hash));
        return authenticationDto;
    }

}
