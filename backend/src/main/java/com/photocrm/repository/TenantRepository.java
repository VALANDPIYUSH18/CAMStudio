package com.photocrm.repository;

import com.photocrm.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

    Optional<TenantEntity> findBySubdomain(String subdomain);

    Optional<TenantEntity> findBySubdomainAndIsActiveTrue(String subdomain);

    @Query("SELECT t FROM TenantEntity t WHERE t.subdomain = :subdomain AND t.isActive = true")
    Optional<TenantEntity> findActiveTenantBySubdomain(@Param("subdomain") String subdomain);

    boolean existsBySubdomain(String subdomain);

    @Query("SELECT COUNT(t) FROM TenantEntity t WHERE t.isActive = true")
    long countActiveTenants();

    @Query("SELECT t FROM TenantEntity t WHERE t.isActive = true ORDER BY t.createdAt DESC")
    java.util.List<TenantEntity> findAllActiveTenants();
}