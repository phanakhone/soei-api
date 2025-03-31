package com.example.soeiapi.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.UpdateUserProfileDto;
import com.example.soeiapi.dtos.UserProfileDto;
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

    public UserProfileDto getProfileByUserId(Long userId) {
        UserProfileEntity userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        return new UserProfileDto(userProfile.getUserProfileId(), userProfile.getFirstName(),
                userProfile.getLastName(), userProfile.getGender(), userProfile.getAddress());
    }

    // update
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN') or " +
            "(hasAnyRole('ADMIN', 'MODERATOR') and @securityService.isSameCompany(#userId)) or " +
            "@securityService.isProfileOwner(#userId)")
    public UserProfileDto updateProfile(Long userId, UpdateUserProfileDto updateUserProfileDto) {
        UserProfileEntity userProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found for userId: " + userId));

        userProfile.setFirstName(updateUserProfileDto.getFirstName());
        userProfile.setLastName(updateUserProfileDto.getLastName());
        userProfile.setGender(updateUserProfileDto.getGender());
        userProfile.setAddress(updateUserProfileDto.getAddress());

        userProfileRepository.save(userProfile);

        return new UserProfileDto(userProfile.getUserProfileId(), userProfile.getFirstName(),
                userProfile.getLastName(), userProfile.getGender(), userProfile.getAddress());
    }

}
