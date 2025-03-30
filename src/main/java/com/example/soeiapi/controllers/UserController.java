package com.example.soeiapi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.Pagination;
import com.example.soeiapi.dtos.UpdateUserProfileDto;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserProfileEntity;
import com.example.soeiapi.services.UserService;
import com.example.soeiapi.services.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping()
    @PostAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getAllUsers(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Map<String, String> filters) {
        // get auth user
        UserEntity authUser = userService
                .getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Set<RoleEntity> roles = authUser.getRoles();
        // check if no role
        if (roles.isEmpty()) {
            throw new IllegalStateException("User has no roles");
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<UserEntity> users;
        if (roles.stream().anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN"))) {
            users = userService.getAllUsers(pageRequest, filters);
        } else {
            users = userService.getAllChildUsersExcludingSameLevel(authUser.getUserId(), pageRequest, filters);
        }

        Pagination pagination = new Pagination(users.getNumber(), users.getSize(), users.getTotalPages(),
                (int) users.getTotalElements(), filters, "id", "asc");

        return ResponseEntity.ok(ApiResponse.successList("Get all users successfully", users.getContent(), pagination));
    }

    @PatchMapping("/{userId}/toggle-enabled")
    @PostAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> setEnabled(@PathVariable Long userId,
            @RequestBody String entity) {

        UserEntity updatedUser = userService.toggleUserEnabled(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User enabled status updated successfully");
        response.put("userId", updatedUser.getUserId());
        response.put("enabled", updatedUser.isEnabled() ? 1 : 0);

        return ResponseEntity.ok(ApiResponse.success("User enabled status updated successfully", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileEntity>> getUserProfile(@PathVariable Long userId) {
        UserProfileEntity userProfile = userProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Fetch user profile successfully", userProfile));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileEntity>> updateUserProfile(
            @PathVariable Long userId, @RequestBody UpdateUserProfileDto updatedUserProfileDto) {

        UserProfileEntity profile = userProfileService.updateProfile(userId, updatedUserProfileDto);

        return ResponseEntity.ok(ApiResponse.success("Update user profile successfully", profile));
    }

}
