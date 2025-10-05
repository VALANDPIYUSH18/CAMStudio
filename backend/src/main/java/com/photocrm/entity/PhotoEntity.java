package com.photocrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "photos")
public class PhotoEntity extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotBlank
    @Size(max = 500)
    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @NotBlank
    @Size(max = 500)
    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Size(max = 500)
    @Column(name = "preview_url")
    private String previewUrl;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotBlank
    @Size(max = 100)
    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected = false;

    @Column(name = "selection_order")
    private Integer selectionOrder;

    @Column(name = "price", precision = 8, scale = 2)
    private BigDecimal price;

    // Constructors
    public PhotoEntity() {
        super();
    }

    public PhotoEntity(UUID tenantId, UUID orderId, String filename, String originalUrl, String thumbnailUrl, 
                      Long fileSize, String mimeType) {
        super(tenantId);
        this.orderId = orderId;
        this.filename = filename;
        this.originalUrl = originalUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Integer getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(Integer selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Helper methods
    public String getFileSizeFormatted() {
        if (fileSize == null) return "0 B";
        
        long bytes = fileSize;
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        
        while (bytes >= 1024 && unitIndex < units.length - 1) {
            bytes /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", (double) bytes, units[unitIndex]);
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public String getFileExtension() {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}