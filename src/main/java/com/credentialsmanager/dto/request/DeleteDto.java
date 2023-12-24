package com.credentialsmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteDto {

    @NotBlank(message = "CurrentMasterPasswordHash cannot be blank")
    private String masterPasswordHash;
}
