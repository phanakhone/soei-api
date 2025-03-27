package com.example.soeiapi.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.soeiapi.entities.*;
import com.example.soeiapi.repositories.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class DataInitializer {

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String ALL_PERMISSION = "ALL";
    private static final String DEFAULT_SUPER_ADMIN_USERNAME = "superadmin";
    private static final String DEFAULT_SUPER_ADMIN_PASSWORD = "$uper@dmin!25";
    private static final String DEFAULT_SUPER_ADMIN_EMAIL = "phanakhone@agl.com.la";
    private static final String DEFAULT_COMPANY_NAME = "Assurance General Laos";
    private static final String DEFAULT_SHORT_NAME = "AGL";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner loadData(RoleRepository roleRepository, PermissionRepository permissionRepository,
            UserRepository userRepository, CompanyRepository companyRepository) {
        return args -> {
            // Initialize roles
            if (roleRepository.count() == 0) {
                roleRepository.save(new RoleEntity(null, SUPER_ADMIN_ROLE, null));
                roleRepository.save(new RoleEntity(null, "ADMIN", null));
                roleRepository.save(new RoleEntity(null, "USER", null));
            }

            // Initialize permissions
            if (permissionRepository.count() == 0) {
                PermissionEntity permission = new PermissionEntity();
                permission.setPermissionName(ALL_PERMISSION);
                permissionRepository.save(permission);
            }

            // Add ALL permission to SUPER_ADMIN role
            RoleEntity superAdminRole = roleRepository.findByRoleName(SUPER_ADMIN_ROLE)
                    .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found"));
            PermissionEntity permission = permissionRepository.findByPermissionName(ALL_PERMISSION)
                    .orElseThrow(() -> new IllegalStateException("ALL permission not found"));

            // Initialize company
            if (companyRepository.count() == 0) {
                CompanyEntity agl = new CompanyEntity();
                agl.setCompanyName(DEFAULT_COMPANY_NAME);
                agl.setCompanyShortName(DEFAULT_SHORT_NAME);
                companyRepository.save(agl);

                CompanyEntity forte = new CompanyEntity();
                forte.setCompanyName("FORTE");
                forte.setCompanyShortName("FORTE");
                companyRepository.save(forte);

                CompanyEntity sokxay = new CompanyEntity();
                sokxay.setCompanyName("SOKXAY");
                sokxay.setCompanyShortName("SOKXAY");
                companyRepository.save(sokxay);
            }

            // Initialize super admin user
            if (userRepository.count() == 0) {
                CompanyEntity company = companyRepository.findByCompanyName(DEFAULT_COMPANY_NAME)
                        .orElseThrow(() -> new IllegalStateException("Default company not found"));

                RoleEntity role = roleRepository.findByRoleName("SUPER_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Default role not found"));

                UserEntity superAdminUser = new UserEntity();
                superAdminUser.setUsername(DEFAULT_SUPER_ADMIN_USERNAME);
                superAdminUser.setPassword(passwordEncoder.encode(DEFAULT_SUPER_ADMIN_PASSWORD));
                superAdminUser.setRoles(Set.of(role));
                superAdminUser.setCompany(company);
                superAdminUser.setEmail(DEFAULT_SUPER_ADMIN_EMAIL);
                superAdminUser.setEnabled(true);
                userRepository.save(superAdminUser);
            }
        };
    }
}