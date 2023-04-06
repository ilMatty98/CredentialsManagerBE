package com.credentialsmanager.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
