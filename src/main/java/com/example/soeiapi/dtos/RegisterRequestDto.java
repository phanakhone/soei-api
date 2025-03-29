package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
    private Long companyId;
    private Integer roleId;
    private Long parentId;
    private Boolean isEnabled;
}
