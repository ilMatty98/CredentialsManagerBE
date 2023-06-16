package com.credentialsmanager.service;

import com.credentialsmanager.dto.request.ChangeEmailDto;
import com.credentialsmanager.dto.request.ChangeInformationDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void changeEmail(ChangeEmailDto changeEmailDto, String oldEmail);

    void changeInformation(ChangeInformationDto changeInformationDto, String email);
}
