package com.example.soeiapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.Pagination;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.services.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Get all roles
    @GetMapping()
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<RoleEntity>>> getAllRoles(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Map<String, String> filters) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<RoleEntity> roles = roleService.getAllRoles(pageRequest, filters);

        Pagination pagination = new Pagination(roles.getNumber(), roles.getSize(), roles.getTotalPages(),
                (int) roles.getTotalElements(), filters, "id", "asc");

        return ResponseEntity.ok(ApiResponse.successList("Get roles successfully", roles.getContent(), pagination));
    }
}
