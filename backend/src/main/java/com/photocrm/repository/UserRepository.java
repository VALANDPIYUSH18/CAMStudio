package com.photocrm.repository;

import com.photocrm.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<UserEntity> findByEmailAndTenantIdAndIsActiveTrue(String email, UUID tenantId);

    List<UserEntity> findByTenantId(UUID tenantId);

    List<UserEntity> findByTenantIdAndIsActiveTrue(UUID tenantId);

    Page<UserEntity> findByTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    List<UserEntity> findByTenantIdAndRole(UUID tenantId, UserEntity.UserRole role);

    List<UserEntity> findByTenantIdAndRoleAndIsActiveTrue(UUID tenantId, UserEntity.UserRole role);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId AND u.role = :role AND u.isActive = true")
    List<UserEntity> findActiveUsersByTenantAndRole(@Param("tenantId") UUID tenantId, @Param("role") UserEntity.UserRole role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.tenantId = :tenantId AND u.isActive = true")
    long countActiveUsersByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId AND u.email LIKE %:email% AND u.isActive = true")
    List<UserEntity> findActiveUsersByTenantAndEmailContaining(@Param("tenantId") UUID tenantId, @Param("email") String email);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId AND u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<UserEntity> findRecentlyActiveUsersByTenant(@Param("tenantId") UUID tenantId, Pageable pageable);
}