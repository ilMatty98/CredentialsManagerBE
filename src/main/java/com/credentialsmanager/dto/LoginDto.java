package com.credentialsmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public abstract class LoginDto {

    private String token;

    private String protectedSymmetricKey;

    @Data
    public static class Request {
        @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @NotBlank(message = "Email cannot be blank")
        private String email;

        @NotBlank(message = "MasterPasswordHash cannot be blank")
        private String masterPasswordHash;
    }

    @Data
    public static class Response {

        private String token;

        private String protectedSymmetricKey;
    }
}
