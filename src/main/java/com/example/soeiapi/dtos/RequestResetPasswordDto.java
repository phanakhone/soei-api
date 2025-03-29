package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class RequestResetPasswordDto {
    private Long userId;
    private Integer tokenExpiryMinutes;
}
