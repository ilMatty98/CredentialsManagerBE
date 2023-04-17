package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface EmailService {

    void sendEmail(String email, String language, EmailTypeEnum emailTypeEnum, Map<String, String> dynamicLabels);
}
