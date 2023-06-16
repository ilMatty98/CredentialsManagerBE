package com.credentialsmanager.dto.request;

import com.credentialsmanager.dto.BaseDto;
import com.credentialsmanager.validator.ChangePasswordValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SignUpDto extends BaseDto {

    @NotBlank(message = "MasterPasswordHash cannot be blank", groups = ChangePasswordValidator.class)
    private String masterPasswordHash;

    @NotBlank(message = "ProtectedSymmetricKey cannot be blank", groups = ChangePasswordValidator.class)
    private String protectedSymmetricKey;

    @NotBlank(message = "InitializationVector cannot be blank", groups = ChangePasswordValidator.class)
    private String initializationVector;

    @Size(max = 100)
    @NotBlank(message = "Hint cannot be blank")
    private String hint;

    @NotBlank(message = "Propic cannot be blank")
    private String propic;

    @Pattern(message = "Language is not valid", regexp = "^[A-Z]{2}$")
    @NotBlank(message = "Language cannot be blank")
    private String language;

}
