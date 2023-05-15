package com.credentialsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeInformationDto extends ChangeDto {

    @Pattern(message = "Language is not valid", regexp = "^[A-Z]{2}$")
    @NotBlank(message = "Language cannot be blank")
    private String language;

    @NotBlank(message = "Hint cannot be blank")
    private String hint;

}
