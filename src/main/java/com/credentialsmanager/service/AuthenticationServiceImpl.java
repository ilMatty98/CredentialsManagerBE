package com.credentialsmanager.service;

import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.entity.Users;
import com.credentialsmanager.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UsersRepository usersRepository;

    @Override
    public AuthenticationDto signIn(AuthenticationDto authenticationDto) {
        var user = new Users();
        user.setEmail(authenticationDto.getEmail());
        user.setSalt("salt");
        user.setHash("hash");
        usersRepository.save(user);
        return authenticationDto;
    }
}
