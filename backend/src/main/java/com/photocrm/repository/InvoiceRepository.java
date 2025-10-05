package com.photocrm.repository;

import com.photocrm.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

    List<InvoiceEntity> findByTenantId(UUID tenantId);

    List<InvoiceEntity> findByTenantIdAndIsActiveTrue(UUID tenantId);

    Page<InvoiceEntity> findByTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    List<InvoiceEntity> findByTenantIdAndStatus(UUID tenantId, InvoiceEntity.InvoiceStatus status);

    List<InvoiceEntity> findByTenantIdAndStatusAndIsActiveTrue(UUID tenantId, InvoiceEntity.InvoiceStatus status);

    List<InvoiceEntity> findByTenantIdAndOrderId(UUID tenantId, UUID orderId);

    List<InvoiceEntity> findByTenantIdAndOrderIdAndIsActiveTrue(UUID tenantId, UUID orderId);

    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

    Optional<InvoiceEntity> findByInvoiceNumberAndTenantId(String invoiceNumber, UUID tenantId);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.dueDate BETWEEN :startDate AND :endDate AND i.isActive = true")
    List<InvoiceEntity> findInvoicesByTenantAndDueDateRange(@Param("tenantId") UUID tenantId, 
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.status = :status AND i.isActive = true ORDER BY i.dueDate ASC")
    List<InvoiceEntity> findInvoicesByTenantAndStatusOrderedByDueDate(@Param("tenantId") UUID tenantId, 
                                                                     @Param("status") InvoiceEntity.InvoiceStatus status);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.dueDate < :currentDate AND i.status != 'PAID' AND i.isActive = true")
    List<InvoiceEntity> findOverdueInvoicesByTenant(@Param("tenantId") UUID tenantId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(i) FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.isActive = true")
    long countActiveInvoicesByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(i) FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.status = :status AND i.isActive = true")
    long countActiveInvoicesByTenantAndStatus(@Param("tenantId") UUID tenantId, @Param("status") InvoiceEntity.InvoiceStatus status);

    @Query("SELECT SUM(i.totalAmount) FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.status = 'PAID' AND i.isActive = true")
    BigDecimal sumPaidAmountByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT SUM(i.totalAmount) FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.status = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate AND i.isActive = true")
    BigDecimal sumPaidAmountByTenantAndDateRange(@Param("tenantId") UUID tenantId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.isActive = true ORDER BY i.createdAt DESC")
    List<InvoiceEntity> findRecentInvoicesByTenant(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.totalAmount >= :minAmount AND i.isActive = true ORDER BY i.totalAmount DESC")
    List<InvoiceEntity> findHighValueInvoicesByTenant(@Param("tenantId") UUID tenantId, @Param("minAmount") BigDecimal minAmount, Pageable pageable);

    Optional<InvoiceEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<InvoiceEntity> findByIdAndTenantIdAndIsActiveTrue(UUID id, UUID tenantId);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.tenantId = :tenantId AND i.invoiceNumber LIKE %:invoiceNumber% AND i.isActive = true")
    List<InvoiceEntity> findInvoicesByTenantAndInvoiceNumberContaining(@Param("tenantId") UUID tenantId, @Param("invoiceNumber") String invoiceNumber);
}