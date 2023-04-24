package com.credentialsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @NotBlank(message = "MasterPasswordHash cannot be blank")
    private String masterPasswordHash;

    @NotBlank(message = "ProtectedSymmetricKey cannot be blank")
    private String protectedSymmetricKey;

    @NotBlank(message = "InitializationVector cannot be blank")
    private String initializationVector;

}
