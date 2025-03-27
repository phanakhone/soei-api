package com.example.soeiapi.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public SecurityService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public boolean isCompanyAdmin(Authentication authentication, Long companyId) {

        String username = authentication.getName();

        return checkUserCompanyRole(username, companyId);
    }

    // Replace with your actual logic to verify the user's role for the specific
    // company
    private boolean checkUserCompanyRole(String username, Long companyId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Assuming CompanyEntity has a relationship with RoleEntity
        // CompanyEntity company = companyRepository.findById(companyId)
        // .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        // Check if the user has the 'ADMIN' role for the given company
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("ADMIN")
                        && companyId.equals(user.getCompany().getCompanyId()));

    }

    public boolean checkUserOwnCompany(String username, Long companyId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getCompany().getCompanyId().equals(companyId);
    }
}
