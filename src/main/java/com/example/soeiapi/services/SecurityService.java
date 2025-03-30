package com.example.soeiapi.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.UserRepository;

@Service
public class SecurityService {
    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
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

    public boolean checkUserOwnCompany(Authentication authentication, Long companyId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // if super admin return true
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN"))) {
            return true;
        }
        return user.getCompany().getCompanyId().equals(companyId);
    }

    public boolean checkAuthUserHasAccessToUser(Authentication authentication, Long userId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserEntity targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // return user.getUserId().equals(userId);

        return isUserUnderHierarchy(user.getUserId(), targetUser);
    }

    // recursive search child
    public boolean isUserUnderHierarchy(Long parentId, UserEntity child) {
        if (child.getParent() == null) {
            return false;
        }
        if (child.getParent().getUserId().equals(parentId)) {
            return true;
        }
        return isUserUnderHierarchy(parentId, child.getParent()); // Recursively check higher levels
    }

    public boolean isSameCompany(Long userId) {
        UserEntity authUser = userRepository.findByUsername(
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return authUser.getCompany().getCompanyId().equals(user.getCompany().getCompanyId());
    }

    public boolean isProfileOwner(Long userId) {
        UserEntity authUser = userRepository.findByUsername(
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return authUser.getUserId().equals(userId);
    }

}
