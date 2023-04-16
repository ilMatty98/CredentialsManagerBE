package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailType;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmail(String email, String language, EmailType emailType);
}
