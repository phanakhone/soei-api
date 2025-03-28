package com.example.soeiapi.dtos;

import java.util.List;

import com.example.soeiapi.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private Long companyId;
    private Integer level;

    public static UserDto fromEntity(UserEntity userEntity) {
        return new UserDto(userEntity.getUserId(), userEntity.getUsername(), userEntity.getEmail(),
                userEntity.getRoles().stream().map(role -> role.getRoleName()).toList(),
                userEntity.getCompany().getCompanyId(), userEntity.getLevel());
    }

}
