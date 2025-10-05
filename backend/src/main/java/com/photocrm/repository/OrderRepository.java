package com.photocrm.repository;

import com.photocrm.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByTenantId(UUID tenantId);

    List<OrderEntity> findByTenantIdAndIsActiveTrue(UUID tenantId);

    Page<OrderEntity> findByTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    List<OrderEntity> findByTenantIdAndStatus(UUID tenantId, OrderEntity.OrderStatus status);

    List<OrderEntity> findByTenantIdAndStatusAndIsActiveTrue(UUID tenantId, OrderEntity.OrderStatus status);

    List<OrderEntity> findByTenantIdAndClientId(UUID tenantId, UUID clientId);

    List<OrderEntity> findByTenantIdAndClientIdAndIsActiveTrue(UUID tenantId, UUID clientId);

    List<OrderEntity> findByTenantIdAndPhotographerId(UUID tenantId, UUID photographerId);

    List<OrderEntity> findByTenantIdAndPhotographerIdAndIsActiveTrue(UUID tenantId, UUID photographerId);

    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.eventDate BETWEEN :startDate AND :endDate AND o.isActive = true")
    List<OrderEntity> findOrdersByTenantAndDateRange(@Param("tenantId") UUID tenantId, 
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.title LIKE %:title% AND o.isActive = true")
    List<OrderEntity> findOrdersByTenantAndTitleContaining(@Param("tenantId") UUID tenantId, @Param("title") String title);

    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.status = :status AND o.isActive = true ORDER BY o.createdAt DESC")
    Page<OrderEntity> findActiveOrdersByTenantAndStatus(@Param("tenantId") UUID tenantId, 
                                                       @Param("status") OrderEntity.OrderStatus status, 
                                                       Pageable pageable);

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.isActive = true")
    long countActiveOrdersByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.status = :status AND o.isActive = true")
    long countActiveOrdersByTenantAndStatus(@Param("tenantId") UUID tenantId, @Param("status") OrderEntity.OrderStatus status);

    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.isActive = true ORDER BY o.createdAt DESC")
    List<OrderEntity> findRecentOrdersByTenant(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId AND o.totalAmount IS NOT NULL AND o.isActive = true ORDER BY o.totalAmount DESC")
    List<OrderEntity> findTopOrdersByRevenue(@Param("tenantId") UUID tenantId, Pageable pageable);

    Optional<OrderEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<OrderEntity> findByIdAndTenantIdAndIsActiveTrue(UUID id, UUID tenantId);
}