package com.credentialsmanager.utils;

import com.credentialsmanager.exception.CustomException;
import com.credentialsmanager.exception.GenericErrorException;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class ControllerUtils {

    public <T> T handleRequest(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }
}
