package com.photocrm.service;

import com.photocrm.entity.PhotoEntity;
import com.photocrm.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private S3Service s3Service;

    public PhotoEntity uploadPhoto(UUID tenantId, UUID orderId, String filename, String originalUrl, 
                                 String thumbnailUrl, Long fileSize, String mimeType) {
        PhotoEntity photo = new PhotoEntity(tenantId, orderId, filename, originalUrl, thumbnailUrl, fileSize, mimeType);
        return photoRepository.save(photo);
    }

    public List<PhotoEntity> batchUploadPhotos(UUID tenantId, UUID orderId, List<PhotoUploadRequest> photos) {
        return photos.stream()
            .map(photoRequest -> uploadPhoto(
                tenantId, 
                orderId, 
                photoRequest.getFilename(), 
                photoRequest.getOriginalUrl(), 
                photoRequest.getThumbnailUrl(), 
                photoRequest.getFileSize(), 
                photoRequest.getMimeType()
            ))
            .toList();
    }

    public PhotoEntity updatePhoto(UUID photoId, UUID tenantId, String filename, BigDecimal price) {
        PhotoEntity photo = photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

        photo.setFilename(filename);
        photo.setPrice(price);

        return photoRepository.save(photo);
    }

    public void selectPhoto(UUID photoId, UUID tenantId, Integer selectionOrder) {
        PhotoEntity photo = photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

        photo.setIsSelected(true);
        photo.setSelectionOrder(selectionOrder);

        photoRepository.save(photo);
    }

    public void deselectPhoto(UUID photoId, UUID tenantId) {
        PhotoEntity photo = photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

        photo.setIsSelected(false);
        photo.setSelectionOrder(null);

        photoRepository.save(photo);
    }

    public void updatePhotoSelection(UUID photoId, UUID tenantId, boolean isSelected, Integer selectionOrder) {
        PhotoEntity photo = photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

        photo.setIsSelected(isSelected);
        photo.setSelectionOrder(isSelected ? selectionOrder : null);

        photoRepository.save(photo);
    }

    public List<PhotoEntity> getPhotosByOrder(UUID orderId) {
        return photoRepository.findByOrderIdAndIsActiveTrue(orderId);
    }

    public List<PhotoEntity> getPhotosByTenantAndOrder(UUID tenantId, UUID orderId) {
        return photoRepository.findByTenantIdAndOrderIdAndIsActiveTrue(tenantId, orderId);
    }

    public List<PhotoEntity> getSelectedPhotosByOrder(UUID orderId) {
        return photoRepository.findByOrderIdAndIsSelectedTrue(orderId);
    }

    public List<PhotoEntity> getSelectedPhotosByTenantAndOrder(UUID tenantId, UUID orderId) {
        return photoRepository.findByTenantIdAndOrderIdAndIsSelectedTrue(tenantId, orderId);
    }

    public Page<PhotoEntity> getPhotosByTenantAndOrder(UUID tenantId, UUID orderId, Pageable pageable) {
        return photoRepository.findPhotosByTenantAndOrder(tenantId, orderId, pageable);
    }

    public PhotoEntity getPhotoByIdAndTenant(UUID photoId, UUID tenantId) {
        return photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));
    }

    public long countPhotosByTenant(UUID tenantId) {
        return photoRepository.countActivePhotosByTenant(tenantId);
    }

    public long countPhotosByTenantAndOrder(UUID tenantId, UUID orderId) {
        return photoRepository.countActivePhotosByTenantAndOrder(tenantId, orderId);
    }

    public long countSelectedPhotosByTenant(UUID tenantId) {
        return photoRepository.countSelectedPhotosByTenant(tenantId);
    }

    public long countSelectedPhotosByTenantAndOrder(UUID tenantId, UUID orderId) {
        return photoRepository.countSelectedPhotosByTenantAndOrder(tenantId, orderId);
    }

    public Long getTotalFileSizeByTenant(UUID tenantId) {
        return photoRepository.sumFileSizeByTenant(tenantId);
    }

    public Long getTotalFileSizeByTenantAndOrder(UUID tenantId, UUID orderId) {
        return photoRepository.sumFileSizeByTenantAndOrder(tenantId, orderId);
    }

    public List<PhotoEntity> getRecentPhotosByTenant(UUID tenantId, Pageable pageable) {
        return photoRepository.findRecentPhotosByTenant(tenantId, pageable);
    }

    public void deletePhoto(UUID photoId, UUID tenantId) {
        PhotoEntity photo = photoRepository.findByIdAndTenantIdAndIsActiveTrue(photoId, tenantId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

        // Delete from S3
        s3Service.deleteFile(photo.getOriginalUrl());
        s3Service.deleteFile(photo.getThumbnailUrl());
        if (photo.getPreviewUrl() != null) {
            s3Service.deleteFile(photo.getPreviewUrl());
        }

        photo.setIsActive(false);
        photoRepository.save(photo);
    }

    public String generatePresignedUploadUrl(UUID tenantId, UUID orderId, String filename, String mimeType) {
        return s3Service.generatePresignedUploadUrl(tenantId, orderId, filename, mimeType);
    }

    public String generatePresignedDownloadUrl(UUID photoId, UUID tenantId) {
        PhotoEntity photo = getPhotoByIdAndTenant(photoId, tenantId);
        return s3Service.generatePresignedDownloadUrl(photo.getOriginalUrl());
    }

    // Inner class for photo upload requests
    public static class PhotoUploadRequest {
        private String filename;
        private String originalUrl;
        private String thumbnailUrl;
        private Long fileSize;
        private String mimeType;

        // Constructors, getters, and setters
        public PhotoUploadRequest() {}

        public PhotoUploadRequest(String filename, String originalUrl, String thumbnailUrl, Long fileSize, String mimeType) {
            this.filename = filename;
            this.originalUrl = originalUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
        }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public String getOriginalUrl() { return originalUrl; }
        public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    }
}