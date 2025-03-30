package com.example.soeiapi.dtos;

// import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserProfileDto {
    private String firstName;

    private String lastName;

    private String gender;
    private String address;
}
