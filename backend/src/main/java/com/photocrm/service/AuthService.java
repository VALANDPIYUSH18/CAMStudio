package com.photocrm.service;

import com.photocrm.config.TenantContext;
import com.photocrm.entity.UserEntity;
import com.photocrm.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> authenticateUser(String email, String password, UUID tenantId) {
        try {
            // Set tenant context
            TenantContext.setCurrentTenant(tenantId);

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email + ":" + tenantId.toString(), password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity user = userService.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Update last login
            userService.updateLastLogin(user.getId(), tenantId);

            // Generate tokens
            String accessToken = jwtUtil.generateToken(userDetails, tenantId, user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails, tenantId, user.getRole().name());

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400); // 24 hours
            response.put("user", createUserResponse(user));

            return response;
        } finally {
            TenantContext.clear();
        }
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        UUID tenantId = jwtUtil.extractTenantId(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        try {
            // Set tenant context
            TenantContext.setCurrentTenant(tenantId);

            // Load user
            UserEntity user = userService.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Create user details
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(role)
                .build();

            // Generate new tokens
            String newAccessToken = jwtUtil.generateToken(userDetails, tenantId, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, tenantId, role);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400); // 24 hours
            response.put("user", createUserResponse(user));

            return response;
        } finally {
            TenantContext.clear();
        }
    }

    public void logout(String authHeader) {
        // In a stateless JWT implementation, logout is handled client-side
        // by removing the token from storage
        // For additional security, you could implement a token blacklist using Redis
    }

    public void sendPasswordResetEmail(String email, String subdomain) {
        // Implementation for sending password reset email
        // This would typically involve:
        // 1. Generate a secure reset token
        // 2. Store the token with expiration
        // 3. Send email with reset link
        throw new RuntimeException("Password reset email functionality not implemented");
    }

    public void resetPassword(String token, String newPassword) {
        // Implementation for password reset
        // This would typically involve:
        // 1. Validate the reset token
        // 2. Update the user's password
        // 3. Invalidate the reset token
        throw new RuntimeException("Password reset functionality not implemented");
    }

    private Map<String, Object> createUserResponse(UserEntity user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("role", user.getRole().name());
        userResponse.put("permissions", user.getPermissions());
        userResponse.put("lastLoginAt", user.getLastLoginAt());
        userResponse.put("createdAt", user.getCreatedAt());
        return userResponse;
    }
}