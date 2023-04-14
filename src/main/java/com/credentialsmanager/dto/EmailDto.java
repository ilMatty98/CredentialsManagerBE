package com.credentialsmanager.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public record EmailDto(String to, String subject, String text) {
}
