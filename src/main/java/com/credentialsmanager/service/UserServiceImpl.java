package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
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

    @Override
    public void changeEmail(ChangeEmailDto changeEmailDto) {
        var user = usersRepository.findByEmailAndState(changeEmailDto.getEmail(), UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_05));

        user.setEmail(changeEmailDto.getNewEmail());
        emailService.sendEmail(user.getEmail(), user.getLanguage(), EmailTypeEnum.CHANGE_EMAIL, new HashMap<>());
        usersRepository.save(user);
    }

    @Override
    public void changeInformation(ChangeInformationDto changeInformationDto) {
        var user = usersRepository.findByEmailAndState(changeInformationDto.getEmail(), UserStateEnum.VERIFIED)
                .orElseThrow(() -> new NotFoundException(MessageEnum.ERROR_05));

        user.setHint(changeInformationDto.getHint());
        user.setLanguage(changeInformationDto.getLanguage());
        usersRepository.save(user);
    }
}
