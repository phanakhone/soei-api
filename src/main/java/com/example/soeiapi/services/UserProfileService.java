package com.example.soeiapi.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.UpdateUserProfileDto;
import com.example.soeiapi.entities.UserProfileEntity;
import com.example.soeiapi.repositories.UserProfileRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileEntity getProfileByUserId(Long userId) {
        return userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));
    }

    // update
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN') or " +
            "(hasAnyRole('ADMIN', 'MODERATOR') and @securityService.isSameCompany(#userId)) or " +
            "@securityService.isProfileOwner(#userId)")
    public UserProfileEntity updateProfile(Long userId, UpdateUserProfileDto updateUserProfileDto) {
        UserProfileEntity userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found for userId: " + userId));

        userProfile.setFirstName(updateUserProfileDto.getFirstName());
        userProfile.setLastName(updateUserProfileDto.getLastName());
        userProfile.setGender(updateUserProfileDto.getGender());
        userProfile.setAddress(updateUserProfileDto.getAddress());

        return userProfileRepository.save(userProfile);
    }

}
