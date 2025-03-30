package com.example.soeiapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminResetPasswordDto {
    @NotNull
    private Long userId;

    @NotBlank
    private String newPassword;
}
