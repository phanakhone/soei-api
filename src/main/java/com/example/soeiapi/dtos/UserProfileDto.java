package com.example.soeiapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private Long userProfileId;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
}
