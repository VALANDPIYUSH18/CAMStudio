package com.photocrm.service;

import com.photocrm.entity.UserEntity;
import com.photocrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Extract tenant from context or from username format (email@domain.com:tenantId)
        String[] parts = username.split(":");
        String email = parts[0];
        UUID tenantId = parts.length > 1 ? UUID.fromString(parts[1]) : null;

        UserEntity user = userRepository.findByEmailAndTenantIdAndIsActiveTrue(email, tenantId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())
            .authorities(user.getRole().name())
            .build();
    }

    public Optional<UserEntity> findByEmailAndTenantId(String email, UUID tenantId) {
        return userRepository.findByEmailAndTenantIdAndIsActiveTrue(email, tenantId);
    }

    public UserEntity createUser(UUID tenantId, String email, String password, String firstName, 
                               String lastName, UserEntity.UserRole role) {
        UserEntity user = new UserEntity(
            tenantId,
            email,
            passwordEncoder.encode(password),
            firstName,
            lastName,
            role
        );
        return userRepository.save(user);
    }

    public UserEntity updateUser(UUID userId, UUID tenantId, String firstName, String lastName, 
                               UserEntity.UserRole role) {
        UserEntity user = userRepository.findByIdAndTenantIdAndIsActiveTrue(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setPermissions(role.getDefaultPermissions());

        return userRepository.save(user);
    }

    public void updateLastLogin(UUID userId, UUID tenantId) {
        UserEntity user = userRepository.findByIdAndTenantIdAndIsActiveTrue(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void changePassword(UUID userId, UUID tenantId, String newPassword) {
        UserEntity user = userRepository.findByIdAndTenantIdAndIsActiveTrue(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deactivateUser(UUID userId, UUID tenantId) {
        UserEntity user = userRepository.findByIdAndTenantIdAndIsActiveTrue(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public List<UserEntity> getUsersByTenant(UUID tenantId) {
        return userRepository.findByTenantIdAndIsActiveTrue(tenantId);
    }

    public List<UserEntity> getUsersByTenantAndRole(UUID tenantId, UserEntity.UserRole role) {
        return userRepository.findByTenantIdAndRoleAndIsActiveTrue(tenantId, role);
    }

    public long countUsersByTenant(UUID tenantId) {
        return userRepository.countActiveUsersByTenant(tenantId);
    }

    public boolean existsByEmailAndTenant(String email, UUID tenantId) {
        return userRepository.existsByEmailAndTenantId(email, tenantId);
    }

    public UserEntity getUserByIdAndTenant(UUID userId, UUID tenantId) {
        return userRepository.findByIdAndTenantIdAndIsActiveTrue(userId, tenantId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}