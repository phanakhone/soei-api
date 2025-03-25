package com.example.soeiapi.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.PermissionEntity;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.RolePermissionId;
import com.example.soeiapi.entities.RolePermissionsEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.PermissionRepository;
import com.example.soeiapi.repositories.RolePermissionsRepository;
import com.example.soeiapi.repositories.RoleRepository;
import com.example.soeiapi.repositories.UserRepository;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadData(RoleRepository roleRepository, PermissionRepository permissionRepository,
            UserRepository userRepository, CompanyRepository companyRepository,
            RolePermissionsRepository rolePermissionsRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                RoleEntity superAdminRole = new RoleEntity();
                superAdminRole.setRoleName("SUPER_ADMIN");
                roleRepository.save(superAdminRole);

                RoleEntity adminRole = new RoleEntity();
                adminRole.setRoleName("ADMIN");
                roleRepository.save(adminRole);

                RoleEntity userRole = new RoleEntity();
                userRole.setRoleName("USER");
                roleRepository.save(userRole);
            }

            if (permissionRepository.count() == 0) {
                // permissions: ALL, MANAGE_SUB_USERS, VIEW_SUB_REPORTS, IMPORT_DATA
                PermissionEntity allPermission = new PermissionEntity();
                allPermission.setPermissionName("ALL");
                permissionRepository.save(allPermission);

                PermissionEntity manageSubUsersPermission = new PermissionEntity();
                manageSubUsersPermission.setPermissionName("MANAGE_SUB_USERS");
                permissionRepository.save(manageSubUsersPermission);

                PermissionEntity viewSubReportsPermission = new PermissionEntity();
                viewSubReportsPermission.setPermissionName("VIEW_SUB_REPORTS");
                permissionRepository.save(viewSubReportsPermission);

                PermissionEntity importDataPermission = new PermissionEntity();
                importDataPermission.setPermissionName("IMPORT_DATA");
                permissionRepository.save(importDataPermission);

            }

            // add ALL permission to SUPER_ADMIN role
            RoleEntity superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN").orElseThrow();
            PermissionEntity permission = permissionRepository.findByPermissionName("ALL").orElseThrow();

            RolePermissionId rolePermissionId = new RolePermissionId();
            rolePermissionId.setRoleId(superAdminRole.getRoleId());
            rolePermissionId.setPermissionId(permission.getPermissionId());

            RolePermissionsEntity rolePermission = new RolePermissionsEntity();
            rolePermission.setId(rolePermissionId);
            rolePermission.setRole(superAdminRole);
            rolePermission.setPermission(permission);
            rolePermissionsRepository.save(rolePermission);

            // create company
            if (companyRepository.count() == 0) {
                CompanyEntity company = new CompanyEntity();
                company.setCompanyName("AGL");
                companyRepository.save(company);
            }

            if (userRepository.count() == 0) {
                // create a super admin user
                // username: superadmin
                // password: superadmin
                // roles: SUPER_ADMIN
                // permissions: ALL

                UserEntity superAdminUser = new UserEntity();
                superAdminUser.setUsername("superadmin");
                superAdminUser.setPassword(passwordEncoder.encode("superadmin"));
                superAdminUser.setRole(roleRepository.findByRoleName("SUPER_ADMIN").orElseThrow());
                superAdminUser.setCompany(companyRepository.findByCompanyName("AGL").orElseThrow());
                superAdminUser.setEmail("phanakhone@agl.com.la");
                userRepository.save(superAdminUser);
            }
        };
    }
}
