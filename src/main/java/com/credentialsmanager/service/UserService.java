package com.credentialsmanager.service;

import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.ChangeInformationDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void changeEmail(ChangeEmailDto changeEmailDto);

    void changeInformation(ChangeInformationDto changeInformationDto);
}
