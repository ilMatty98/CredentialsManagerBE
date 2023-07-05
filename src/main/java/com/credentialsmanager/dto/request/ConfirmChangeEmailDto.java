package com.credentialsmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ConfirmChangeEmailDto extends ChangeEmailDto {

    @NotBlank(message = "VerificationCode cannot be blank")
    private String verificationCode;

}
