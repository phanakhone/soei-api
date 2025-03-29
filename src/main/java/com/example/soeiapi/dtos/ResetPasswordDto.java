package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String resetPasswordToken;
    private String newPassword;
}
