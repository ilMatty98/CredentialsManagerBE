package com.credentialsmanager.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super(message);
        log.error(message);
    }
}
