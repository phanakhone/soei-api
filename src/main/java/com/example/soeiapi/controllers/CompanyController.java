package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.CreateCompanyRequestDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.services.CompanyService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping()
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CompanyEntity>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CompanyEntity>> createCompany(
            @RequestBody CreateCompanyRequestDto createCompanyRequestDto) {
        CompanyEntity company = companyService.createCompany(createCompanyRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Company create successfuly", company));
    }

}
