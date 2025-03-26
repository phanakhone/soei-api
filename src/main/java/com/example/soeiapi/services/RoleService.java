package com.example.soeiapi.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.repositories.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

}
