package com.restaurant.backend.auth.service;

import com.restaurant.backend.admin.user.entity.User;
import com.restaurant.backend.admin.user.repository.UserRepository;
import com.restaurant.backend.auth.dto.LoginRequest;
import com.restaurant.backend.auth.dto.LoginResponse;
import com.restaurant.backend.auth.dto.UserInfoResponse;
import com.restaurant.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service for login and user info.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and return JWT token.
     */
    public LoginResponse login(LoginRequest request) {
        log.info("[AUTH_LOGIN] username={}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!user.getEnabled()) {
            throw new BusinessException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("[AUTH_LOGIN_FAILED] username={}", request.getUsername());
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name());

        log.info("[AUTH_LOGIN_SUCCESS] userId={}, role={}", user.getId(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /**
     * Get current user info from user ID.
     */
    public UserInfoResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
