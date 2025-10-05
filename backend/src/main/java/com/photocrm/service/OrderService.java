package com.photocrm.service;

import com.photocrm.entity.OrderEntity;
import com.photocrm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderEntity createOrder(UUID tenantId, UUID clientId, String title, String description, 
                                 LocalDate eventDate, UUID photographerId) {
        OrderEntity order = new OrderEntity(tenantId, clientId, title, OrderEntity.OrderStatus.CREATED);
        order.setDescription(description);
        order.setEventDate(eventDate);
        order.setPhotographerId(photographerId);
        
        // Generate gallery URL and QR code
        order.setGalleryUrl(generateGalleryUrl(order.getId()));
        order.setQrCode(generateQRCode(order.getId()));
        
        return orderRepository.save(order);
    }

    public OrderEntity updateOrder(UUID orderId, UUID tenantId, String title, String description, 
                                 LocalDate eventDate, UUID photographerId) {
        OrderEntity order = orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTitle(title);
        order.setDescription(description);
        order.setEventDate(eventDate);
        order.setPhotographerId(photographerId);

        return orderRepository.save(order);
    }

    public void updateOrderStatus(UUID orderId, UUID tenantId, OrderEntity.OrderStatus status) {
        OrderEntity order = orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }

    public void assignPhotographer(UUID orderId, UUID tenantId, UUID photographerId) {
        OrderEntity order = orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPhotographerId(photographerId);
        orderRepository.save(order);
    }

    public void updateTotalAmount(UUID orderId, UUID tenantId, BigDecimal totalAmount) {
        OrderEntity order = orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    public List<OrderEntity> getOrdersByTenant(UUID tenantId) {
        return orderRepository.findByTenantIdAndIsActiveTrue(tenantId);
    }

    public Page<OrderEntity> getOrdersByTenant(UUID tenantId, Pageable pageable) {
        return orderRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
    }

    public List<OrderEntity> getOrdersByTenantAndStatus(UUID tenantId, OrderEntity.OrderStatus status) {
        return orderRepository.findByTenantIdAndStatusAndIsActiveTrue(tenantId, status);
    }

    public List<OrderEntity> getOrdersByClient(UUID tenantId, UUID clientId) {
        return orderRepository.findByTenantIdAndClientIdAndIsActiveTrue(tenantId, clientId);
    }

    public List<OrderEntity> getOrdersByPhotographer(UUID tenantId, UUID photographerId) {
        return orderRepository.findByTenantIdAndPhotographerIdAndIsActiveTrue(tenantId, photographerId);
    }

    public List<OrderEntity> getOrdersByDateRange(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        return orderRepository.findOrdersByTenantAndDateRange(tenantId, startDate, endDate);
    }

    public OrderEntity getOrderByIdAndTenant(UUID orderId, UUID tenantId) {
        return orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public long countOrdersByTenant(UUID tenantId) {
        return orderRepository.countActiveOrdersByTenant(tenantId);
    }

    public long countOrdersByTenantAndStatus(UUID tenantId, OrderEntity.OrderStatus status) {
        return orderRepository.countActiveOrdersByTenantAndStatus(tenantId, status);
    }

    public List<OrderEntity> getRecentOrdersByTenant(UUID tenantId, Pageable pageable) {
        return orderRepository.findRecentOrdersByTenant(tenantId, pageable);
    }

    public List<OrderEntity> getTopOrdersByRevenue(UUID tenantId, Pageable pageable) {
        return orderRepository.findTopOrdersByRevenue(tenantId, pageable);
    }

    public void deleteOrder(UUID orderId, UUID tenantId) {
        OrderEntity order = orderRepository.findByIdAndTenantIdAndIsActiveTrue(orderId, tenantId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setIsActive(false);
        orderRepository.save(order);
    }

    private String generateGalleryUrl(UUID orderId) {
        return "https://photocrm.com/gallery/" + orderId.toString();
    }

    private String generateQRCode(UUID orderId) {
        // In a real implementation, this would generate an actual QR code
        // For now, return a placeholder
        return "QR_CODE_DATA_FOR_ORDER_" + orderId.toString();
    }
}