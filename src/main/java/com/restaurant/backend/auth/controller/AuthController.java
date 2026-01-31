package com.restaurant.backend.auth.controller;

import com.restaurant.backend.auth.dto.LoginRequest;
import com.restaurant.backend.auth.dto.LoginResponse;
import com.restaurant.backend.auth.dto.UserInfoResponse;
import com.restaurant.backend.auth.security.UserPrincipal;
import com.restaurant.backend.auth.service.AuthService;
import com.restaurant.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for login and user info.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login with username and password.
     * 
     * @param request LoginRequest with username and password
     * @return JWT token and user info
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * Get current authenticated user info.
     * 
     * @param userPrincipal Current user from security context
     * @return User info (id, username, role)
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInfoResponse response = authService.getCurrentUser(userPrincipal.getId());
        return ApiResponse.success(response);
    }
}
