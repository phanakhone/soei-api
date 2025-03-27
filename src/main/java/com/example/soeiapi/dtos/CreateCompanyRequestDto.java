package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class CreateCompanyRequestDto {
    private String companyAddress;
    private String companyEmail;
    private String companyHotlineAccident;
    private String companyHotlineSale;
    private String companyName;
    private String companyShortName;
    private String companyPhoneNumber;
    private String companyWebsite;

    // generate comment with json post
    // {
    // "companyAddress": "string",
    // "companyEmail": "string",
    // "companyHotlineAccident": "string",
    // "companyHotlineSale": "string",
    // "companyName": "string",
    // "companyShortName": "string",
    // "companyPhoneNumber": "string",
    // "companyWebsite": "string"
    // }

}
