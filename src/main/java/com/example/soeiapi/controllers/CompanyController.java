package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.CreateCompanyRequestDto;
import com.example.soeiapi.dtos.Pagination;
import com.example.soeiapi.dtos.UpdateCompanyDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.services.CompanyService;
import com.example.soeiapi.services.SecurityService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    private final SecurityService securityService;

    public CompanyController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CompanyEntity>>> getAllRoles(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Map<String, String> filters) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<CompanyEntity> companies = companyService.getAllCompanies(pageRequest, filters);

        Pagination pagination = new Pagination(companies.getNumber(), companies.getSize(), companies.getTotalPages(),
                (int) companies.getTotalElements(), filters, "id", "asc");

        return ResponseEntity
                .ok(ApiResponse.successList("Get companies successfully", companies.getContent(), pagination));
    }

    @PostMapping()

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CompanyEntity>> createCompany(
            @RequestBody CreateCompanyRequestDto createCompanyRequestDto) {
        CompanyEntity company = companyService.createCompany(createCompanyRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Company create successfully", company));
    }

    @GetMapping("/{companyId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and @securityService.checkUserOwnCompany(authentication, #companyId))")
    public ResponseEntity<CompanyEntity> getCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyService.getCompany(companyId));
    }

    @PutMapping("/{companyId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and @securityService.checkUserOwnCompany(authentication, #companyId))")
    public ResponseEntity<ApiResponse<CompanyEntity>> updateCompany(@PathVariable Long companyId,
            @RequestBody UpdateCompanyDto updateCompanyDto) {
        CompanyEntity company = companyService.updateCompany(companyId, updateCompanyDto);
        return ResponseEntity.ok(ApiResponse.success("Company update successfully", company));
    }

}
