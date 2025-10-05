package com.photocrm.controller;

import com.photocrm.config.TenantContext;
import com.photocrm.entity.OrderEntity;
import com.photocrm.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            OrderEntity order = orderService.createOrder(
                tenantId,
                request.getClientId(),
                request.getTitle(),
                request.getDescription(),
                request.getEventDate(),
                request.getPhotographerId()
            );

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            Page<OrderEntity> orders;
            if (search != null && !search.isEmpty()) {
                orders = orderService.getOrdersByTenant(tenantId, pageable);
            } else if (status != null && !status.isEmpty()) {
                List<OrderEntity> orderList = orderService.getOrdersByTenantAndStatus(
                    tenantId, 
                    OrderEntity.OrderStatus.valueOf(status)
                );
                return ResponseEntity.ok(orderList);
            } else {
                orders = orderService.getOrdersByTenant(tenantId, pageable);
            }

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable UUID orderId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            OrderEntity order = orderService.getOrderByIdAndTenant(orderId, tenantId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID orderId, 
                                       @Valid @RequestBody UpdateOrderRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            OrderEntity order = orderService.updateOrder(
                orderId,
                tenantId,
                request.getTitle(),
                request.getDescription(),
                request.getEventDate(),
                request.getPhotographerId()
            );

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID orderId, 
                                             @RequestBody UpdateStatusRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            orderService.updateOrderStatus(orderId, tenantId, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/assign")
    public ResponseEntity<?> assignPhotographer(@PathVariable UUID orderId, 
                                              @RequestBody AssignPhotographerRequest request) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            orderService.assignPhotographer(orderId, tenantId, request.getPhotographerId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
        try {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                return ResponseEntity.badRequest().body("Tenant context not found");
            }

            orderService.deleteOrder(orderId, tenantId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Request DTOs
    public static class CreateOrderRequest {
        private UUID clientId;
        private String title;
        private String description;
        private LocalDate eventDate;
        private UUID photographerId;

        public UUID getClientId() { return clientId; }
        public void setClientId(UUID clientId) { this.clientId = clientId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getEventDate() { return eventDate; }
        public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
        public UUID getPhotographerId() { return photographerId; }
        public void setPhotographerId(UUID photographerId) { this.photographerId = photographerId; }
    }

    public static class UpdateOrderRequest {
        private String title;
        private String description;
        private LocalDate eventDate;
        private UUID photographerId;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getEventDate() { return eventDate; }
        public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
        public UUID getPhotographerId() { return photographerId; }
        public void setPhotographerId(UUID photographerId) { this.photographerId = photographerId; }
    }

    public static class UpdateStatusRequest {
        private OrderEntity.OrderStatus status;

        public OrderEntity.OrderStatus getStatus() { return status; }
        public void setStatus(OrderEntity.OrderStatus status) { this.status = status; }
    }

    public static class AssignPhotographerRequest {
        private UUID photographerId;

        public UUID getPhotographerId() { return photographerId; }
        public void setPhotographerId(UUID photographerId) { this.photographerId = photographerId; }
    }
}