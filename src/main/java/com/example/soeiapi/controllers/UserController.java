package com.example.soeiapi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.Pagination;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @PostAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getAllUsers(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Map<String, String> filters) {
        // TODO: filter by auth user
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserEntity> users = userService.getAllUsers(pageRequest, filters);

        Pagination pagination = new Pagination(users.getNumber(), users.getSize(), users.getTotalPages(),
                (int) users.getTotalElements(), filters, "id", "asc");

        return ResponseEntity.ok(ApiResponse.successList("Get all users successfully", users.getContent(), pagination));
    }

    @PatchMapping("/{userId}/toggle-enabled")
    @PostAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> setEnabled(@PathVariable Long userId,
            @RequestBody String entity) {

        UserEntity updatedUser = userService.toggleUserEnabled(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User enabled status updated successfully");
        response.put("userId", updatedUser.getUserId());
        response.put("enabled", updatedUser.isEnabled() ? 1 : 0);

        return ResponseEntity.ok(ApiResponse.success("User enabled status updated successfully", response));
    }

}
