package com.photocrm.repository;

import com.photocrm.entity.PhotoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

    List<PhotoEntity> findByTenantId(UUID tenantId);

    List<PhotoEntity> findByTenantIdAndIsActiveTrue(UUID tenantId);

    List<PhotoEntity> findByOrderId(UUID orderId);

    List<PhotoEntity> findByOrderIdAndIsActiveTrue(UUID orderId);

    List<PhotoEntity> findByTenantIdAndOrderId(UUID tenantId, UUID orderId);

    List<PhotoEntity> findByTenantIdAndOrderIdAndIsActiveTrue(UUID tenantId, UUID orderId);

    List<PhotoEntity> findByTenantIdAndIsSelectedTrue(UUID tenantId);

    List<PhotoEntity> findByOrderIdAndIsSelectedTrue(UUID orderId);

    List<PhotoEntity> findByTenantIdAndOrderIdAndIsSelectedTrue(UUID tenantId, UUID orderId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isSelected = :isSelected AND p.isActive = true")
    List<PhotoEntity> findPhotosByTenantAndOrderAndSelectionStatus(@Param("tenantId") UUID tenantId, 
                                                                  @Param("orderId") UUID orderId, 
                                                                  @Param("isSelected") Boolean isSelected);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.mimeType LIKE 'image/%' AND p.isActive = true")
    List<PhotoEntity> findImagePhotosByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.uploadedAt BETWEEN :startDate AND :endDate AND p.isActive = true")
    List<PhotoEntity> findPhotosByTenantAndDateRange(@Param("tenantId") UUID tenantId, 
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.filename LIKE %:filename% AND p.isActive = true")
    List<PhotoEntity> findPhotosByTenantAndFilenameContaining(@Param("tenantId") UUID tenantId, @Param("filename") String filename);

    @Query("SELECT COUNT(p) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.isActive = true")
    long countActivePhotosByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(p) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isActive = true")
    long countActivePhotosByTenantAndOrder(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId);

    @Query("SELECT COUNT(p) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.isSelected = true AND p.isActive = true")
    long countSelectedPhotosByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(p) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isSelected = true AND p.isActive = true")
    long countSelectedPhotosByTenantAndOrder(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId);

    @Query("SELECT SUM(p.fileSize) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.isActive = true")
    Long sumFileSizeByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT SUM(p.fileSize) FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isActive = true")
    Long sumFileSizeByTenantAndOrder(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.isActive = true ORDER BY p.uploadedAt DESC")
    List<PhotoEntity> findRecentPhotosByTenant(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isActive = true ORDER BY p.uploadedAt ASC")
    List<PhotoEntity> findPhotosByTenantAndOrderOrderedByUploadTime(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isSelected = true AND p.isActive = true ORDER BY p.selectionOrder ASC")
    List<PhotoEntity> findSelectedPhotosByTenantAndOrderOrderedBySelection(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId);

    Optional<PhotoEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<PhotoEntity> findByIdAndTenantIdAndIsActiveTrue(UUID id, UUID tenantId);

    @Query("SELECT p FROM PhotoEntity p WHERE p.tenantId = :tenantId AND p.orderId = :orderId AND p.isActive = true")
    Page<PhotoEntity> findPhotosByTenantAndOrder(@Param("tenantId") UUID tenantId, @Param("orderId") UUID orderId, Pageable pageable);
}