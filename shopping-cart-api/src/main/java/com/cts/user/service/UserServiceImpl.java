package com.cts.user.service;

import com.cts.common.error.ApiException;
import com.cts.user.api.RoleUpdateRequest;
import com.cts.user.api.UpdateProfileRequest;
import com.cts.user.api.UserResponse;
import com.cts.user.domain.AppUser;
import com.cts.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User"));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User"));

        user.setDisplayName(request.displayName());
        return mapToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserRoles(Long userId, RoleUpdateRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User"));

        user.setRoles(request.roles());
        return mapToUserResponse(userRepository.save(user));
    }

    private UserResponse mapToUserResponse(AppUser user) {
        Set<String> roleStrings = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
                
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                roleStrings
        );
    }
}