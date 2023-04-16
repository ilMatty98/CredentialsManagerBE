package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmail(String email, String language, EmailTypeEnum emailTypeEnum);
}
