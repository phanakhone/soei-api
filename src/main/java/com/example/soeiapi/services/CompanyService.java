package com.example.soeiapi.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.CreateCompanyRequestDto;
import com.example.soeiapi.dtos.UpdateCompanyDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.repositories.CompanyRepository;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public Page<CompanyEntity> getAllCompanies(PageRequest pageRequest, Map<String, String> filters) {
        return companyRepository.findAll(pageRequest);
    }

    public CompanyEntity getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("Company not found"));
    }

    // create company
    public CompanyEntity createCompany(CreateCompanyRequestDto createCompanyRequestDto) {
        CompanyEntity company = new CompanyEntity();
        company.setCompanyName(createCompanyRequestDto.getCompanyName());
        company.setCompanyShortName(createCompanyRequestDto.getCompanyShortName());
        company.setCompanyAddress(createCompanyRequestDto.getCompanyAddress());
        company.setCompanyPhoneNumber(createCompanyRequestDto.getCompanyPhoneNumber());
        company.setCompanyEmail(createCompanyRequestDto.getCompanyEmail());
        company.setCompanyWebsite(createCompanyRequestDto.getCompanyWebsite());
        company.setCompanyHotlineSale(createCompanyRequestDto.getCompanyHotlineSale());
        company.setCompanyHotlineAccident(createCompanyRequestDto.getCompanyHotlineAccident());

        return companyRepository.save(company);
    }

    // update company
    public CompanyEntity updateCompany(Long companyId, UpdateCompanyDto updateCompanyDto) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("Company not found"));

        company.setCompanyName(updateCompanyDto.getCompanyName());
        company.setCompanyShortName(updateCompanyDto.getCompanyShortName());
        company.setCompanyAddress(updateCompanyDto.getCompanyAddress());
        company.setCompanyPhoneNumber(updateCompanyDto.getCompanyPhoneNumber());
        company.setCompanyEmail(updateCompanyDto.getCompanyEmail());
        company.setCompanyWebsite(updateCompanyDto.getCompanyWebsite());
        company.setCompanyHotlineSale(updateCompanyDto.getCompanyHotlineSale());
        company.setCompanyHotlineAccident(updateCompanyDto.getCompanyHotlineAccident());

        return companyRepository.save(company);
    }
}
