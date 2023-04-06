package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UsersRepository;
import com.credentialsmanager.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UsersRepository usersRepository;

    private final AuthenticationMapper authenticationMapper;

    @Override
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        if (usersRepository.existsById(authenticationDto.getEmail()))
            throw new BadRequestException(MessageUtils.ERROR_01.getMessage());

        usersRepository.save(authenticationMapper.dtotoUser(authenticationDto, "salt", "hash"));
        return authenticationDto;
    }
}
