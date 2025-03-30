package com.example.soeiapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordWithTokenDto {
    @NotBlank
    private String resetPasswordToken;

    @NotBlank
    private String newPassword;
}
