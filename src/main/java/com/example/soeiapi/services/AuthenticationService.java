package com.example.soeiapi.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.AuthResponse;
import com.example.soeiapi.dtos.LoginRequestDto;
import com.example.soeiapi.dtos.RegisterRequestDto;
import com.example.soeiapi.dtos.UserDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.RoleRepository;
import com.example.soeiapi.repositories.UserRepository;
import com.example.soeiapi.security.JwtUtil;

@Service
public class AuthenticationService {

    private final UserRefreshTokenService userRefreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    public AuthenticationService(UserRefreshTokenService userRefreshTokenService, JwtUtil jwtUtil,
            UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            CompanyRepository companyRepository) {
        this.userRefreshTokenService = userRefreshTokenService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
    }

    public AuthResponse register(RegisterRequestDto registerRequestDto) {
        // get auth user
        UserEntity authUser = userRepository
                .findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check username and email exists
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEnabled(registerRequestDto.getIsEnabled() == null ? false : registerRequestDto.getIsEnabled());

        if (authUser != null) {
            user.setCreatedBy(authUser.getUsername());
        }

        // if role is SUPER_ADMIN
        if (authUser != null
                && authUser.getRoles().stream().anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()))) {
            if (registerRequestDto.getCompanyId() == null) {
                throw new RuntimeException("Company ID is required for SUPER_ADMIN");
            }
            if (registerRequestDto.getRoleId() == null) {
                throw new RuntimeException("Role ID is required for SUPER_ADMIN");
            }

            CompanyEntity company = companyRepository.findById(registerRequestDto.getCompanyId()).orElseThrow(
                    () -> new RuntimeException("Company with id " + registerRequestDto.getCompanyId() + " not found"));
            user.setCompany(company);

            // check if role exists
            RoleEntity role = roleRepository.findById(registerRequestDto.getRoleId()).orElseThrow(
                    () -> new RuntimeException("Role with id " + registerRequestDto.getRoleId() + " not found"));
            user.setRoles(Set.of(role));

            // check if parent user exists
            UserEntity authenticatedUser = userRepository.findById(registerRequestDto.getParentId()).orElseThrow(
                    () -> new RuntimeException(
                            "Parent user with id " + registerRequestDto.getParentId() + " not found"));
            user.setParent(authenticatedUser);
        } else {
            if (authUser != null) {
                user.setCompany(authUser.getCompany());
            } else {
                throw new RuntimeException("Authenticated user is null");
            }

            RoleEntity userRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
            user.setRoles(Set.of(userRole));

            user.setParent(authUser);
        }

        userRepository.save(user);

        return new AuthResponse(null, null, UserDto.fromEntity(user));
    }

    public AuthResponse login(LoginRequestDto loginRequestDto) {
        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new RuntimeException("User does not exist"));

        // **Check if user is enabled**
        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled. Contact admin.");
        }

        // check if password is correct
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Map<String, String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));

        String token = jwtUtil.generateToken(claims, user.getUsername());
        String refreshToken = userRefreshTokenService.createUserRefreshToken(user);

        // update last login date time
        userRepository.updateLastLogin(user.getUserId());

        return new AuthResponse(token, refreshToken, UserDto.fromEntity(user));
    }

    // refresh token
    public AuthResponse refreshToken(String refreshToken) {
        // check if refresh token valid
        UserRefreshTokenEntity userRefreshToken = userRefreshTokenService.validateRefreshToken(refreshToken);

        // Get the user associated with the refresh token
        UserEntity user = userRepository.findById(userRefreshToken.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found in refresh token"));

        // check if user is enabled
        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled. Contact admin.");
        }

        Map<String, String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));

        String newToken = jwtUtil.generateToken(claims, user.getUsername());
        String newRefreshToken = userRefreshTokenService.createUserRefreshToken(user);

        return new AuthResponse(newToken, newRefreshToken, UserDto.fromEntity(user));
    }
}
