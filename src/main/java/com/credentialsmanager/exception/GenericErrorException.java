package com.credentialsmanager.exception;

import com.credentialsmanager.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericErrorException extends RuntimeException {

    public GenericErrorException(Throwable t) {
        super(MessageUtils.ERROR_00.getLabel());
        log.error(MessageUtils.ERROR_00.getLabel(), t);
    }
}
