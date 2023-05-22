package com.credentialsmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.sql.Timestamp;

public interface LogInDto {

    @Data
    class Request {
        @Email(message = "Email is not valid", regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
        @NotBlank(message = "Email cannot be blank")
        private String email;

        @NotBlank(message = "MasterPasswordHash cannot be blank")
        private String masterPasswordHash;

        @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")
        @NotBlank(message = "IpAddress cannot be blank")
        private String ipAddress;

        @NotBlank(message = "DeviceType cannot be blank")
        private String deviceType;

        @NotBlank(message = "LocalDateTime cannot be blank")
        private String localDateTime;
    }

    @Data
    class Response {

        private String token;

        private String tokenPublicKey;

        private String protectedSymmetricKey;

        private String initializationVector;

        private String language;

        private Timestamp timestampCreation;

        private Timestamp timestampLastAccess;

        private Timestamp timestampPassword;
    }
}
