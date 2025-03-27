package com.example.soeiapi.dtos;

import lombok.Data;

@Data
public class UpdateCompanyDto {
    private String companyAddress;
    private String companyEmail;
    private String companyHotlineAccident;
    private String companyHotlineSale;
    private String companyName;
    private String companyShortName;
    private String companyPhoneNumber;
    private String companyWebsite;
}
