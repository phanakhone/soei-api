package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String email;
    private String phoneNumber;
    private Boolean isEnabled;
    // roles
}
