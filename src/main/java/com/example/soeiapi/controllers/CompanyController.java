package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.services.CompanyService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

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

}
