package com.credentialsmanager.service;

import com.credentialsmanager.dto.EmailDto;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    boolean sendEmail(EmailDto emailDto);
}
