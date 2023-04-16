package com.credentialsmanager.exception;

import com.credentialsmanager.constants.MessageEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericErrorException extends RuntimeException {

    public GenericErrorException(Throwable t) {
        super(MessageEnum.ERROR_00.getLabel());
        log.error(MessageEnum.ERROR_00.getLabel(), t);
    }
}
