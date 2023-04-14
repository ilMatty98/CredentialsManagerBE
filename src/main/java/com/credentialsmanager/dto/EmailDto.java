package com.credentialsmanager.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailDto {

    private final String from;

    private final String to;

    private final String cc;

    private final String subject;

    private final String text;
}
