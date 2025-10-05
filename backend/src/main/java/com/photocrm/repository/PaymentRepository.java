package com.photocrm.repository;

import com.photocrm.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByTenantId(UUID tenantId);

    List<PaymentEntity> findByTenantIdAndIsActiveTrue(UUID tenantId);

    Page<PaymentEntity> findByTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    List<PaymentEntity> findByTenantIdAndStatus(UUID tenantId, PaymentEntity.PaymentStatus status);

    List<PaymentEntity> findByTenantIdAndStatusAndIsActiveTrue(UUID tenantId, PaymentEntity.PaymentStatus status);

    List<PaymentEntity> findByTenantIdAndInvoiceId(UUID tenantId, UUID invoiceId);

    List<PaymentEntity> findByTenantIdAndInvoiceIdAndIsActiveTrue(UUID tenantId, UUID invoiceId);

    List<PaymentEntity> findByTenantIdAndProvider(UUID tenantId, PaymentEntity.PaymentProvider provider);

    List<PaymentEntity> findByTenantIdAndProviderAndIsActiveTrue(UUID tenantId, PaymentEntity.PaymentProvider provider);

    Optional<PaymentEntity> findByProviderTransactionId(String providerTransactionId);

    Optional<PaymentEntity> findByProviderTransactionIdAndTenantId(String providerTransactionId, UUID tenantId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.paidAt BETWEEN :startDate AND :endDate AND p.isActive = true")
    List<PaymentEntity> findPaymentsByTenantAndDateRange(@Param("tenantId") UUID tenantId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.status = :status AND p.isActive = true ORDER BY p.createdAt DESC")
    List<PaymentEntity> findPaymentsByTenantAndStatusOrderedByCreatedDate(@Param("tenantId") UUID tenantId, 
                                                                         @Param("status") PaymentEntity.PaymentStatus status);

    @Query("SELECT COUNT(p) FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.isActive = true")
    long countActivePaymentsByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(p) FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.status = :status AND p.isActive = true")
    long countActivePaymentsByTenantAndStatus(@Param("tenantId") UUID tenantId, @Param("status") PaymentEntity.PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.status = 'SUCCEEDED' AND p.isActive = true")
    BigDecimal sumSuccessfulAmountByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.status = 'SUCCEEDED' AND p.paidAt BETWEEN :startDate AND :endDate AND p.isActive = true")
    BigDecimal sumSuccessfulAmountByTenantAndDateRange(@Param("tenantId") UUID tenantId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.isActive = true ORDER BY p.createdAt DESC")
    List<PaymentEntity> findRecentPaymentsByTenant(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.amount >= :minAmount AND p.isActive = true ORDER BY p.amount DESC")
    List<PaymentEntity> findHighValuePaymentsByTenant(@Param("tenantId") UUID tenantId, @Param("minAmount") BigDecimal minAmount, Pageable pageable);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.providerTransactionId LIKE %:transactionId% AND p.isActive = true")
    List<PaymentEntity> findPaymentsByTenantAndTransactionIdContaining(@Param("tenantId") UUID tenantId, @Param("transactionId") String transactionId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.invoiceId = :invoiceId AND p.status = 'SUCCEEDED' AND p.isActive = true")
    List<PaymentEntity> findSuccessfulPaymentsByTenantAndInvoice(@Param("tenantId") UUID tenantId, @Param("invoiceId") UUID invoiceId);

    @Query("SELECT COUNT(p) FROM PaymentEntity p WHERE p.tenantId = :tenantId AND p.invoiceId = :invoiceId AND p.status = 'SUCCEEDED' AND p.isActive = true")
    long countSuccessfulPaymentsByTenantAndInvoice(@Param("tenantId") UUID tenantId, @Param("invoiceId") UUID invoiceId);

    Optional<PaymentEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<PaymentEntity> findByIdAndTenantIdAndIsActiveTrue(UUID id, UUID tenantId);
}