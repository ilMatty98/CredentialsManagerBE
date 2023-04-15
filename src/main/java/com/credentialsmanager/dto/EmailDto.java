package com.credentialsmanager.dto;

import com.credentialsmanager.constants.EmailType;

public record EmailDto(String to, String language, EmailType emailType) {
}
