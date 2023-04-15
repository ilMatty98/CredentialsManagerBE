package com.credentialsmanager.exception;

import com.credentialsmanager.constants.MessageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends CustomException {

    public NotFoundException(MessageUtils messageUtils) {
        super(messageUtils);
        log.error(messageUtils.getMessage());
    }
}
