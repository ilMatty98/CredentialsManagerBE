package com.credentialsmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailDto {

    @JsonIgnore
    private String currentEmail;

    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email cannot be blank")
    private String newEmail;

    @NotBlank(message = "MasterPasswordHash cannot be blank")
    private String masterPasswordHash;

}
