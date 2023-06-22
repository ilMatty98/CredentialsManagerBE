package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ChangeEmailDto;
import com.credentialsmanager.dto.request.ChangeInformationDto;
import com.credentialsmanager.exception.NotFoundException;
import com.credentialsmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmailService emailService;

    private final UserRepository usersRepository;

    private final AuthenticationService authenticationService;

    @Override
    public void changeEmail(ChangeEmailDto changeEmailDto, String oldEmail) {
        var user = usersRepository.findByEmailAndState(oldEmail, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_05));

        authenticationService.checkPassword(user, changeEmailDto.getMasterPasswordHash());

        user.setEmail(changeEmailDto.getEmail());
        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.CHANGE_EMAIL, new HashMap<>());
        usersRepository.save(user);
    }

    @Override
    public void changeInformation(ChangeInformationDto changeInformationDto, String email) {
        var user = usersRepository.findByEmailAndState(email, UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_05));

        user.setHint(changeInformationDto.getHint());
        user.setLanguage(changeInformationDto.getLanguage());
        user.setPropic(changeInformationDto.getPropic());
        usersRepository.save(user);
    }
}
