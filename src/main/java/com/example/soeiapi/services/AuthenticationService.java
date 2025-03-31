package com.example.soeiapi.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.AdminResetPasswordDto;
import com.example.soeiapi.dtos.AuthResponse;
import com.example.soeiapi.dtos.LoginRequestDto;
import com.example.soeiapi.dtos.RegisterRequestDto;
import com.example.soeiapi.dtos.ResetPasswordWithTokenDto;
import com.example.soeiapi.dtos.UserDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserProfileEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;
import com.example.soeiapi.entities.UserResetPasswordTokenEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.RoleRepository;
import com.example.soeiapi.repositories.UserRepository;
import com.example.soeiapi.repositories.UserResetPasswordTokenRespository;
import com.example.soeiapi.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthenticationService {

    private final UserRefreshTokenService userRefreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserResetPasswordTokenRespository userResetPasswordTokenRepository;
    private final MailService emailService;
    private final SecurityService securityService;

    public AuthenticationService(UserRefreshTokenService userRefreshTokenService, JwtUtil jwtUtil,
            UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            CompanyRepository companyRepository, UserResetPasswordTokenRespository userResetPasswordTokenRepository,
            MailService emailService, SecurityService securityService) {
        this.emailService = emailService;
        this.userRefreshTokenService = userRefreshTokenService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.userResetPasswordTokenRepository = userResetPasswordTokenRepository;
        this.securityService = securityService;
    }

    public UserDetails getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return (UserDetails) principal; // Return username from UserDetails
            } else {
                throw new RuntimeException("pricipal is not instance of UserDetails");
            }
        }

        throw new RuntimeException("User is not authenticated");
    }

    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        return user;
    }

    // function generate username
    public String generateUsername(long companyId, Integer roleId) {
        // generate username format companyShortName + roleId + 000001(get last 6 digit
        // of latest row in users)
        String companyShortName = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found")).getCompanyShortName();
        UserEntity lastUser = userRepository.findLatestUserByCompanyIdAndRoleId(companyId, roleId).orElse(null);

        String username = companyShortName + String.format("%02d", roleId);
        if (lastUser != null && lastUser.getUsername().length() > 6) {
            String lastUserIdStr = lastUser.getUsername().substring(lastUser.getUsername().length() - 5);
            int lastUserIdInt = Integer.parseInt(lastUserIdStr);
            lastUserIdInt++;
            String lastUserIdNew = String.format("%05d", lastUserIdInt);
            username += lastUserIdNew;
        } else {
            username += "00001";
        }
        return username;
    }

    public AuthResponse register(RegisterRequestDto registerRequestDto) {
        // get auth user
        UserEntity authUser = userRepository
                .findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isSuperAdmin = authUser.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));
        // generate username or get username
        String username = registerRequestDto.getUsername() != null ? registerRequestDto.getUsername()
                : generateUsername(
                        isSuperAdmin ? registerRequestDto.getCompanyId() : authUser.getCompany().getCompanyId(),
                        registerRequestDto.getRoleId());

        // check username and email exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
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

            // check if parentId user exists
            UserEntity parentUser;
            if (registerRequestDto.getParentId() == null) {
                parentUser = null;
            } else {
                parentUser = userRepository.findById(registerRequestDto.getParentId()).orElse(null);
            }

            if (parentUser == null) {
                user.setParent(parentUser);
            } else {
                user.setParent(null);
            }

        } else {
            if (authUser != null) {
                user.setCompany(authUser.getCompany());
            } else {
                throw new RuntimeException("Authenticated user is null");
            }

            RoleEntity userRole;
            if (registerRequestDto.getRoleId() != null) {
                userRole = roleRepository.findById(registerRequestDto.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
            } else {
                userRole = roleRepository.findByRoleName("USER").orElseThrow(
                        () -> new RuntimeException("Role 'USER' not found"));
            }

            user.setRoles(Set.of(userRole));

            user.setParent(authUser);
        }

        // Create empty profile
        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setUser(user);

        user.setProfile(userProfile);

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

    // request password reset

    @Transactional
    public void requestPasswordReset(Long userId, Integer tokenExpiryMinutes) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // remove old token
        userResetPasswordTokenRepository.deleteByUserId(user.getUserId());

        String token = UUID.randomUUID().toString();
        UserResetPasswordTokenEntity resetToken = new UserResetPasswordTokenEntity();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiredAt(Instant.now().plus(
                tokenExpiryMinutes != null ? tokenExpiryMinutes : 60, ChronoUnit.MINUTES));
        resetToken.setCreatedAt(Instant.now());

        userResetPasswordTokenRepository.save(resetToken);

        emailService.sendResetPasswordEmailByUserId(user.getUserId(), tokenExpiryMinutes);
    }

    // reset password
    @Transactional
    public void resetPasswordWithToken(ResetPasswordWithTokenDto resetPasswordWithTokenDto) {
        String token = resetPasswordWithTokenDto.getResetPasswordToken();
        String newPassword = resetPasswordWithTokenDto.getNewPassword();

        UserResetPasswordTokenEntity resetToken = userResetPasswordTokenRepository.findByToken(token).orElseThrow(
                () -> new RuntimeException("Invalid or expired reset password token"));

        UserEntity user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setEnabled(true); // Enable the user after password reset
        user.setVerified(true); // Mark the user as verified after password reset
        user.setLastPasswordChangeAt(Instant.now());

        // Update last login time
        userRepository.save(user);

        // Invalidate token after use
        userResetPasswordTokenRepository.delete(resetToken);
    }

    @Transactional
    public void resetPasswordByAdmin(String authUsername, AdminResetPasswordDto adminResetPasswordDto) {
        Long userId = adminResetPasswordDto.getUserId();
        String newPassword = adminResetPasswordDto.getNewPassword();

        Long authUserId = userRepository
                .findByUsername(authUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isSuperAdmin = userRepository
                .findByUsername(authUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getRoles()
                .stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));

        boolean isAdmin = userRepository
                .findByUsername(authUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));

        boolean isModerator = userRepository
                .findByUsername(authUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getRoles()
                .stream()
                .anyMatch(role -> "MODERATOR".equals(role.getRoleName()));

        if (!isSuperAdmin || !isAdmin || !isModerator) {

            if (securityService.isUserUnderHierarchy(authUserId, user)) {
                throw new RuntimeException("You do not have permission to reset this user's password");
            }
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setEnabled(true); // Enable the user after password reset
        user.setVerified(true); // Mark the user as verified after password reset
        user.setLastPasswordChangeAt(Instant.now());

        // Update last login time
        userRepository.save(user);

        // Invalidate token after use
        userResetPasswordTokenRepository.deleteByUserId(userId);
    }
}
