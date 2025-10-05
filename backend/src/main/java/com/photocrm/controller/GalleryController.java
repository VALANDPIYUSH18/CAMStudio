package com.photocrm.controller;

import com.photocrm.config.TenantContext;
import com.photocrm.entity.PhotoEntity;
import com.photocrm.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gallery")
@CrossOrigin(origins = "*")
public class GalleryController {

    @Autowired
    private PhotoService photoService;

    @GetMapping("/{orderId}/public")
    public ResponseEntity<?> getPublicGallery(@PathVariable UUID orderId) {
        try {
            // For public gallery, we don't require tenant context
            // The orderId should be sufficient to identify the gallery
            List<PhotoEntity> photos = photoService.getPhotosByOrder(orderId);
            
            PublicGalleryResponse response = new PublicGalleryResponse();
            response.setOrderId(orderId);
            response.setPhotos(photos);
            response.setTotalPhotos(photos.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/selections")
    public ResponseEntity<?> submitSelections(@PathVariable UUID orderId, 
                                            @Valid @RequestBody PhotoSelectionRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            // Update photo selections
            for (int i = 0; i < request.getSelectedPhotoIds().size(); i++) {
                UUID photoId = request.getSelectedPhotoIds().get(i);
                photoService.updatePhotoSelection(photoId, tenantId, true, i + 1);
            }

            // Calculate total amount
            List<PhotoEntity> selectedPhotos = photoService.getSelectedPhotosByTenantAndOrder(tenantId, orderId);
            double totalAmount = selectedPhotos.stream()
                .mapToDouble(photo -> photo.getPrice() != null ? photo.getPrice().doubleValue() : 0.0)
                .sum();

            PhotoSelectionResponse response = new PhotoSelectionResponse();
            response.setSelectedCount(selectedPhotos.size());
            response.setTotalAmount(totalAmount);
            response.setTax(totalAmount * 0.1); // 10% tax
            response.setFinalAmount(totalAmount * 1.1);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/selections")
    public ResponseEntity<?> getCurrentSelections(@PathVariable UUID orderId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            List<PhotoEntity> selectedPhotos = photoService.getSelectedPhotosByTenantAndOrder(tenantId, orderId);
            return ResponseEntity.ok(selectedPhotos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/selections")
    public ResponseEntity<?> updateSelections(@PathVariable UUID orderId, 
                                            @Valid @RequestBody PhotoSelectionRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            // First, deselect all photos for this order
            List<PhotoEntity> allPhotos = photoService.getPhotosByTenantAndOrder(tenantId, orderId);
            for (PhotoEntity photo : allPhotos) {
                if (photo.getIsSelected()) {
                    photoService.deselectPhoto(photo.getId(), tenantId);
                }
            }

            // Then select the new photos
            for (int i = 0; i < request.getSelectedPhotoIds().size(); i++) {
                UUID photoId = request.getSelectedPhotoIds().get(i);
                photoService.updatePhotoSelection(photoId, tenantId, true, i + 1);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Request/Response DTOs
    public static class PhotoSelectionRequest {
        private List<UUID> selectedPhotoIds;
        private ClientInfo clientInfo;

        public List<UUID> getSelectedPhotoIds() { return selectedPhotoIds; }
        public void setSelectedPhotoIds(List<UUID> selectedPhotoIds) { this.selectedPhotoIds = selectedPhotoIds; }
        public ClientInfo getClientInfo() { return clientInfo; }
        public void setClientInfo(ClientInfo clientInfo) { this.clientInfo = clientInfo; }
    }

    public static class ClientInfo {
        private String name;
        private String email;
        private String phone;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class PublicGalleryResponse {
        private UUID orderId;
        private List<PhotoEntity> photos;
        private int totalPhotos;
        private Integer selectionLimit;

        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public List<PhotoEntity> getPhotos() { return photos; }
        public void setPhotos(List<PhotoEntity> photos) { this.photos = photos; }
        public int getTotalPhotos() { return totalPhotos; }
        public void setTotalPhotos(int totalPhotos) { this.totalPhotos = totalPhotos; }
        public Integer getSelectionLimit() { return selectionLimit; }
        public void setSelectionLimit(Integer selectionLimit) { this.selectionLimit = selectionLimit; }
    }

    public static class PhotoSelectionResponse {
        private int selectedCount;
        private double totalAmount;
        private double tax;
        private double finalAmount;

        public int getSelectedCount() { return selectedCount; }
        public void setSelectedCount(int selectedCount) { this.selectedCount = selectedCount; }
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        public double getTax() { return tax; }
        public void setTax(double tax) { this.tax = tax; }
        public double getFinalAmount() { return finalAmount; }
        public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    }
}