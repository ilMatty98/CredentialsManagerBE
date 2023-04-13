package com.credentialsmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegistrationDto {

    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "MasterPasswordHash cannot be blank")
    private String masterPasswordHash;

    @NotBlank(message = "ProtectedSymmetricKey cannot be blank")
    private String protectedSymmetricKey;

    @NotBlank(message = "InitializationVector cannot be blank")
    private String initializationVector;

}
