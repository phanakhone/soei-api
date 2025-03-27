package com.example.soeiapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    // private String expiresAt;
    private UserDto user;

    // @Data
    // public static class UserDto {
    // private Long id;
    // private String username;
    // private String email;
    // private String fullName;
    // private String role;
    // private Long companyId;
    // private String companyName;
    // }

}
