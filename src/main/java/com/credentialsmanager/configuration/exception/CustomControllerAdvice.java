package com.credentialsmanager.configuration.exception;

import com.credentialsmanager.exception.BadRequestException;
import com.credentialsmanager.exception.GenericErrorException;
import com.credentialsmanager.exception.NotFoundException;
import com.credentialsmanager.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
class CustomControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        var status = HttpStatus.NOT_FOUND;
        var printWriter = new PrintWriter(new StringWriter());
        e.printStackTrace(printWriter);
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        var status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception e) {
        var status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }

    @ExceptionHandler(GenericErrorException.class)
    public ResponseEntity<ErrorResponse> handleGenericErrorException(Exception e) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }
}
