package com.credentialsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeInformationDto extends ChangeDto {

    @Pattern(message = "Language is not valid", regexp = "^[A-Z]{2}$")
    @NotBlank(message = "Language cannot be blank")
    private String language;

    @Size(max = 100)
    @NotBlank(message = "Hint cannot be blank")
    private String hint;

    @NotBlank(message = "Propic cannot be blank")
    private String propic;

}
