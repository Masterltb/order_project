package com.restaurant.backend.auth.dto;

import com.restaurant.backend.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login response DTO with JWT token and user info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
}
