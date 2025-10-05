package com.photocrm.controller;

import com.photocrm.config.TenantContext;
import com.photocrm.entity.PhotoEntity;
import com.photocrm.service.PhotoService;
import com.photocrm.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/photos")
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/batch-upload")
    public ResponseEntity<?> batchUploadPhotos(@Valid @RequestBody BatchUploadRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            List<PhotoEntity> photos = photoService.batchUploadPhotos(
                tenantId,
                request.getOrderId(),
                request.getPhotos()
            );

            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPhotosByOrder(@PathVariable UUID orderId, Pageable pageable) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            Page<PhotoEntity> photos = photoService.getPhotosByTenantAndOrder(tenantId, orderId, pageable);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<?> getPhoto(@PathVariable UUID photoId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            PhotoEntity photo = photoService.getPhotoByIdAndTenant(photoId, tenantId);
            return ResponseEntity.ok(photo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{photoId}")
    public ResponseEntity<?> updatePhoto(@PathVariable UUID photoId, 
                                       @Valid @RequestBody UpdatePhotoRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            PhotoEntity photo = photoService.updatePhoto(
                photoId,
                tenantId,
                request.getFilename(),
                request.getPrice()
            );

            return ResponseEntity.ok(photo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{photoId}/select")
    public ResponseEntity<?> selectPhoto(@PathVariable UUID photoId, 
                                       @RequestBody SelectPhotoRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            photoService.selectPhoto(photoId, tenantId, request.getSelectionOrder());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{photoId}/deselect")
    public ResponseEntity<?> deselectPhoto(@PathVariable UUID photoId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            photoService.deselectPhoto(photoId, tenantId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{photoId}/download")
    public ResponseEntity<?> getDownloadUrl(@PathVariable UUID photoId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            String downloadUrl = photoService.generatePresignedDownloadUrl(photoId, tenantId);
            return ResponseEntity.ok(new DownloadUrlResponse(downloadUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-url")
    public ResponseEntity<?> getUploadUrl(@Valid @RequestBody UploadUrlRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            String uploadUrl = photoService.generatePresignedUploadUrl(
                tenantId,
                request.getOrderId(),
                request.getFilename(),
                request.getMimeType()
            );

            return ResponseEntity.ok(new UploadUrlResponse(uploadUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable UUID photoId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            photoService.deletePhoto(photoId, tenantId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Request/Response DTOs
    public static class BatchUploadRequest {
        private UUID orderId;
        private List<PhotoUploadData> photos;

        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public List<PhotoUploadData> getPhotos() { return photos; }
        public void setPhotos(List<PhotoUploadData> photos) { this.photos = photos; }
    }

    public static class PhotoUploadData {
        private String filename;
        private String originalUrl;
        private String thumbnailUrl;
        private Long fileSize;
        private String mimeType;

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

    public static class UpdatePhotoRequest {
        private String filename;
        private BigDecimal price;

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static class SelectPhotoRequest {
        private Integer selectionOrder;

        public Integer getSelectionOrder() { return selectionOrder; }
        public void setSelectionOrder(Integer selectionOrder) { this.selectionOrder = selectionOrder; }
    }

    public static class UploadUrlRequest {
        private UUID orderId;
        private String filename;
        private String mimeType;

        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    }

    public static class UploadUrlResponse {
        private String uploadUrl;

        public UploadUrlResponse(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public String getUploadUrl() { return uploadUrl; }
        public void setUploadUrl(String uploadUrl) { this.uploadUrl = uploadUrl; }
    }

    public static class DownloadUrlResponse {
        private String downloadUrl;

        public DownloadUrlResponse(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    }
}