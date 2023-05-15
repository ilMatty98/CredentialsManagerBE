package com.credentialsmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeEmailDto extends ChangeDto {

    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email cannot be blank")
    private String newEmail;

    @NotBlank(message = "MasterPasswordHash cannot be blank")
    private String masterPasswordHash;

}
