package com.example.soeiapi.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.soeiapi.entities.*;
import com.example.soeiapi.repositories.*;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class DataInitializer {

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String ALL_PERMISSION = "ALL";
    private static final String DEFAULT_SUPER_ADMIN_USERNAME = "superadmin";
    private static final String DEFAULT_SUPER_ADMIN_PASSWORD = "superadmin";
    private static final String DEFAULT_SUPER_ADMIN_EMAIL = "phanakhone@agl.com.la";
    private static final String DEFAULT_COMPANY_NAME = "AGL";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner loadData(RoleRepository roleRepository, PermissionRepository permissionRepository,
            UserRepository userRepository, CompanyRepository companyRepository,
            RolePermissionsRepository rolePermissionsRepository) {
        return args -> {
            // Initialize roles
            if (roleRepository.count() == 0) {
                roleRepository.save(new RoleEntity(null, SUPER_ADMIN_ROLE, null));
                roleRepository.save(new RoleEntity(null, "ADMIN", null));
                roleRepository.save(new RoleEntity(null, "USER", null));
            }

            // Initialize permissions
            if (permissionRepository.count() == 0) {
                permissionRepository.save(new PermissionEntity(null, ALL_PERMISSION, null));
                permissionRepository.save(new PermissionEntity(null, "MANAGE_SUB_USERS", null));
                permissionRepository.save(new PermissionEntity(null, "VIEW_SUB_REPORTS", null));
                permissionRepository.save(new PermissionEntity(null, "IMPORT_DATA", null));
            }

            // Add ALL permission to SUPER_ADMIN role
            RoleEntity superAdminRole = roleRepository.findByRoleName(SUPER_ADMIN_ROLE)
                    .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found"));
            PermissionEntity permission = permissionRepository.findByPermissionName(ALL_PERMISSION)
                    .orElseThrow(() -> new IllegalStateException("ALL permission not found"));

            RolePermissionId rolePermissionId = new RolePermissionId(superAdminRole.getRoleId(),
                    permission.getPermissionId());
            if (!rolePermissionsRepository.existsById(rolePermissionId)) {
                rolePermissionsRepository.save(new RolePermissionsEntity(rolePermissionId, superAdminRole, permission));
            }

            // Initialize company
            if (companyRepository.count() == 0) {
                CompanyEntity agl = new CompanyEntity();
                agl.setCompanyName(DEFAULT_COMPANY_NAME);
                companyRepository.save(agl);

                CompanyEntity forte = new CompanyEntity();
                forte.setCompanyName("FORTE");
                companyRepository.save(forte);

                CompanyEntity sokxay = new CompanyEntity();
                sokxay.setCompanyName("SOKXAY");
                companyRepository.save(sokxay);
            }

            // Initialize super admin user
            if (userRepository.count() == 0) {
                CompanyEntity company = companyRepository.findByCompanyName(DEFAULT_COMPANY_NAME)
                        .orElseThrow(() -> new IllegalStateException("Default company not found"));

                UserEntity superAdminUser = new UserEntity();
                superAdminUser.setUsername(DEFAULT_SUPER_ADMIN_USERNAME);
                superAdminUser.setPassword(passwordEncoder.encode(DEFAULT_SUPER_ADMIN_PASSWORD));
                superAdminUser.setRole(superAdminRole);
                superAdminUser.setCompany(company);
                superAdminUser.setEmail(DEFAULT_SUPER_ADMIN_EMAIL);
                userRepository.save(superAdminUser);
            }
        };
    }
}